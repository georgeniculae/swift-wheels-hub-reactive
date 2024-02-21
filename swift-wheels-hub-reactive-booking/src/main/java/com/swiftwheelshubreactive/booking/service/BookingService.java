package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.aspect.LogActivity;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_OF_BOOKING = "dateOfBooking";
    private final BookingRepository bookingRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CarService carService;
    private final EmployeeService employeeService;
    private final OutboxService outboxService;
    private final BookingMapper bookingMapper;

    public Flux<BookingResponse> findAllBookings() {
        return bookingRepository.findAll()
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding bookings: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BookingResponse> findBookingById(String id) {
        return findEntityById(id)
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding booking by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<BookingResponse> findBookingsByDateOfBooking(String dateOfBooking) {
        return reactiveMongoTemplate.find(getQuery(dateOfBooking), Booking.class)
                .map(bookingMapper::mapEntityToDto)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Booking from date: " + dateOfBooking + " does not exist"
                                )
                        )
                )
                .onErrorResume(e -> {
                    log.error("Error while finding booking by date of booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<BookingResponse> findBookingsByLoggedInUser(String username) {
        return bookingRepository.findByCustomerUsername(username)
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding bookings by logged in customer: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BigDecimal> getAmountSpentByLoggedInUser(String username) {
        return findBookingsByLoggedInUser(username)
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<BigDecimal> getSumOfAllBookingAmount() {
        return findAllBookings()
                .map(BookingResponse::amount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Mono<Long> countBookings() {
        return bookingRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting bookings: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countBookingsOfLoggedInUser(String username) {
        return bookingRepository.countByCustomerUsername(username)
                .onErrorResume(e -> {
                    log.error("Error while counting bookings of logged in user: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<LocalDate> getCurrentDate() {
        return Mono.just(LocalDate.now());
    }

    @LogActivity(
            activityDescription = "Booking creation",
            sentParameters = "newBookingRequest"
    )
    public Mono<BookingResponse> saveBooking(String apiKey, List<String> roles, BookingRequest newBookingRequest) {
        return validateBookingDates(newBookingRequest)
                .flatMap(bookingRequest -> carService.findAvailableCarById(apiKey, roles, bookingRequest.carId()))
                .map(carResponse -> setupNewBooking(newBookingRequest, carResponse))
                .flatMap(booking -> outboxService.saveBookingAndOutboxTransactional(booking, Outbox.Operation.CREATE))
                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                .flatMap(bookingResponse -> setCarForNewBooking(apiKey, roles, bookingResponse))
                .onErrorResume(e -> {
                    log.error("Error while saving booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "Booking update",
            sentParameters = "id"
    )
    public Mono<BookingResponse> updateBooking(String apiKey, List<String> roles, String id,
                                               BookingRequest updatedBookingRequest) {
        return validateBookingDates(updatedBookingRequest)
                .flatMap(bookingRequest -> findEntityById(id))
                .flatMap(existingBooking -> getCarIfIsChanged(apiKey, roles, updatedBookingRequest, existingBooking)
                        .map(carResponse -> updateExistingBookingWithNewCarDetails(updatedBookingRequest, existingBooking, carResponse))
                        .switchIfEmpty(Mono.defer(() -> Mono.just(updateExistingBooking(updatedBookingRequest, existingBooking))))
                        .flatMap(updatedExistingBooking -> outboxService.saveBookingAndOutboxTransactional(updatedExistingBooking, Outbox.Operation.UPDATE)
                                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                                .flatMap(savedBookingResponse -> changeCarsStatus(apiKey, roles, savedBookingResponse, existingBooking))))
                .onErrorResume(e -> {
                    log.error("Error while updating booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BookingResponse> closeBooking(String apiKeySecret, List<String> roles, BookingClosingDetails bookingClosingDetails) {
        return findEntityById(bookingClosingDetails.bookingId())
                .flatMap(existingBooking -> employeeService.findEmployeeById(apiKeySecret, roles, bookingClosingDetails.receptionistEmployeeId())
                        .map(employeeResponse -> {
                            existingBooking.setStatus(BookingStatus.CLOSED);
                            existingBooking.setReturnBranchId(MongoUtil.getObjectId(employeeResponse.workingBranchId()));

                            return existingBooking;
                        }))
                .flatMap(bookingRepository::save)
                .map(bookingMapper::mapEntityToDto)
                .flatMap(bookingResponse -> updateCarWhenBookingIsClosed(apiKeySecret, roles, bookingResponse, bookingClosingDetails))
                .onErrorResume(e -> {
                    log.error("Error while closing booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "Booking deletion",
            sentParameters = "id"
    )
    public Mono<Void> deleteBookingById(String apiKey, List<String> roles, String id) {
        return findEntityById(id)
                .flatMap(booking -> outboxService.processBookingDeletion(booking, Outbox.Operation.DELETE))
                .flatMap(booking -> carService.changeCarStatus(apiKey, roles, booking.getCarId().toString(), CarState.AVAILABLE))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while deleting booking by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Void> processBookingDeletion(String username) {
        return bookingRepository.deleteByCustomerUsername(username);
    }

    private Mono<BookingRequest> validateBookingDates(BookingRequest newBookingRequest) {
        return Mono.fromSupplier(() -> {
            LocalDate dateFrom = newBookingRequest.dateFrom();
            LocalDate dateTo = newBookingRequest.dateTo();
            LocalDate currentDate = LocalDate.now();

            if (dateFrom.isBefore(currentDate) || dateTo.isBefore(currentDate)) {
                throw new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A date of booking cannot be in the past"
                );
            }

            if (dateFrom.isAfter(dateTo)) {
                throw new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Date from is after date to"
                );
            }

            return newBookingRequest;
        });
    }

    private Mono<BookingResponse> updateCarWhenBookingIsClosed(String apiKey, List<String> roles,
                                                               BookingResponse bookingResponse, BookingClosingDetails bookingClosingDetails) {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(bookingResponse.carId())
                .receptionistEmployeeId(bookingClosingDetails.receptionistEmployeeId())
                .carState(bookingClosingDetails.carState())
                .build();

        return carService.updateCarWhenBookingIsFinished(apiKey, roles, carUpdateDetails)
                .map(carResponse -> bookingResponse);
    }

    private Mono<BookingResponse> setCarForNewBooking(String apiKey, List<String> roles, BookingResponse bookingResponse) {
        return carService.changeCarStatus(apiKey, roles, bookingResponse.carId(), CarState.NOT_AVAILABLE)
                .map(carResponse -> bookingResponse);
    }

    private Mono<CarResponse> getCarIfIsChanged(String apiKey, List<String> roles, BookingRequest updatedBookingRequest,
                                                Booking existingBooking) {
        return Mono.just(updatedBookingRequest.carId())
                .filter(id -> !existingBooking.getCarId().toString().equals(id))
                .flatMap(newCarId -> carService.findAvailableCarById(apiKey, roles, newCarId))
                .switchIfEmpty(Mono.empty());
    }

    private Query getQuery(String dateOfBooking) {
        Date dateOfBookingAsDate;
        Date dayAfterDateOfBookingAsDate;

        try {
            dateOfBookingAsDate = new SimpleDateFormat(DATE_FORMAT).parse(dateOfBooking);

            dayAfterDateOfBookingAsDate = new SimpleDateFormat(DATE_FORMAT)
                    .parse(LocalDate.parse(dateOfBooking)
                            .plusDays(1)
                            .format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        } catch (ParseException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }

        Criteria dateOfBookingCriteria = Criteria.where(DATE_OF_BOOKING)
                .gte(dateOfBookingAsDate)
                .lt(dayAfterDateOfBookingAsDate);

        return new Query().addCriteria(dateOfBookingCriteria);
    }

    private Mono<Booking> findEntityById(String id) {
        return bookingRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Booking with id " + id + " does not exist"
                                )
                        )
                );
    }

    private Booking setupNewBooking(BookingRequest newBookingDto, CarResponse carResponse) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingDto);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(newBooking.getCustomerUsername());
        newBooking.setCustomerEmail(newBookingDto.customerEmail());
        newBooking.setCarId(MongoUtil.getObjectId(carResponse.id()));
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(MongoUtil.getObjectId(carResponse.actualBranchId()));
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBooking.getDateFrom(), newBooking.getDateTo(), amount));
        newBooking.setRentalCarPrice(amount);

        return newBooking;
    }

    private Booking updateExistingBooking(BookingRequest updatedBookingRequest, Booking existingBooking) {
        LocalDate dateFrom = updatedBookingRequest.dateFrom();
        LocalDate dateTo = updatedBookingRequest.dateTo();

        existingBooking.setDateFrom(dateFrom);
        existingBooking.setDateTo(dateTo);
        existingBooking.setAmount(getAmount(dateFrom, dateTo, existingBooking.getRentalCarPrice()));

        return existingBooking;
    }

    private Booking updateExistingBookingWithNewCarDetails(BookingRequest updatedBookingRequest, Booking existingBooking,
                                                           CarResponse carResponse) {
        LocalDate dateFrom = updatedBookingRequest.dateFrom();
        LocalDate dateTo = updatedBookingRequest.dateTo();

        Booking updatedExistingBooking = bookingMapper.getNewBooking(existingBooking);
        BigDecimal amount = carResponse.amount();

        updatedExistingBooking.setDateFrom(dateFrom);
        updatedExistingBooking.setDateTo(dateTo);
        updatedExistingBooking.setCarId(MongoUtil.getObjectId(carResponse.id()));
        updatedExistingBooking.setRentalBranchId(MongoUtil.getObjectId(carResponse.actualBranchId()));
        updatedExistingBooking.setAmount(getAmount(dateFrom, dateTo, amount));
        updatedExistingBooking.setRentalCarPrice(amount);

        return updatedExistingBooking;
    }

    private Mono<BookingResponse> changeCarsStatus(String apiKey, List<String> roles,
                                                   BookingResponse updatedBookingResponse, Booking existingBooking) {
        return Mono.just(existingBooking.getCarId().toString())
                .filter(existingBookingCarId -> !existingBookingCarId.equals(updatedBookingResponse.carId()))
                .flatMap(existingBookingCarId -> {
                    List<UpdateCarRequest> updateCarRequests =
                            getCarsForStatusUpdate(existingBookingCarId, updatedBookingResponse.carId());

                    return carService.updateCarsStatus(apiKey, roles, updateCarRequests)
                            .collectList()
                            .map(carResponses -> updatedBookingResponse);
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(updatedBookingResponse)));
    }

    private List<UpdateCarRequest> getCarsForStatusUpdate(String existingCarId, String newCarId) {
        return List.of(
                new UpdateCarRequest(existingCarId, CarState.AVAILABLE),
                new UpdateCarRequest(newCarId, CarState.NOT_AVAILABLE)
        );
    }

    private BigDecimal getAmount(LocalDate dateFrom, LocalDate dateTo, BigDecimal amount) {
        int bookingDays = Period.between(dateFrom, dateTo).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return amount.multiply(BigDecimal.valueOf(bookingDays));
    }

}
