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
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
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
                .onErrorMap(e -> {
                    log.error("Error while finding bookings: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<BookingResponse> findBookingById(String id) {
        return findEntityById(id)
                .map(bookingMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding booking by id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<BookingResponse> findBookingsByDateOfBooking(String dateOfBooking) {
        return reactiveMongoTemplate.find(getQuery(dateOfBooking), Booking.class)
                .map(bookingMapper::mapEntityToDto)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Booking from date: " + dateOfBooking + " does not exist")))
                .onErrorMap(e -> {
                    log.error("Error while finding booking by date of booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<BookingResponse> findBookingsByLoggedInUser(String username) {
        return bookingRepository.findByCustomerUsername(username)
                .map(bookingMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding bookings by logged in customer: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
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
                .onErrorMap(e -> {
                    log.error("Error while counting bookings: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Long> countBookingsOfLoggedInUser(String username) {
        return bookingRepository.countByCustomerUsername(username)
                .onErrorMap(e -> {
                    log.error("Error while counting bookings of logged in user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
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
                .flatMap(booking -> outboxService.saveBookingAndOutbox(booking, Outbox.Operation.CREATE))
                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                .delayUntil(bookingResponse -> setCarForNewBooking(apiKey, roles, bookingResponse))
                .onErrorMap(e -> {
                    log.error("Error while saving booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
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
                .flatMap(existingBooking -> updateExistingBooking(apiKey, roles, updatedBookingRequest, existingBooking))
                .onErrorMap(e -> {
                    log.error("Error while updating booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<BookingResponse> closeBooking(String apiKeySecret, List<String> roles, BookingClosingDetails bookingClosingDetails) {
        return findEntityById(bookingClosingDetails.bookingId())
                .flatMap(existingBooking -> updatedBookingWithEmployeeDetails(apiKeySecret, roles, bookingClosingDetails, existingBooking))
                .flatMap(bookingRepository::save)
                .map(bookingMapper::mapEntityToDto)
                .delayUntil(bookingResponse -> updateCarWhenBookingIsClosed(apiKeySecret, roles, bookingResponse, bookingClosingDetails))
                .onErrorMap(e -> {
                    log.error("Error while closing booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @LogActivity(
            activityDescription = "Booking deletion",
            sentParameters = "id"
    )
    public Mono<Void> deleteBookingByCustomerUsername(String apiKey, List<String> roles, String username) {
        return bookingRepository.findByCustomerUsername(username)
                .collectList()
                .flatMap(bookings -> outboxService.processBookingDeletion(bookings, Outbox.Operation.DELETE))
                .flatMap(carsIds -> carService.updateCarsStatus(apiKey, roles, getUpdateCarRequest(carsIds)))
                .onErrorMap(e -> {
                    log.error("Error while deleting booking by username: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private List<UpdateCarRequest> getUpdateCarRequest(List<String> bookingsIds) {
        return bookingsIds.stream()
                .map(id -> new UpdateCarRequest(id, CarState.AVAILABLE))
                .toList();
    }

    private Mono<BookingRequest> validateBookingDates(BookingRequest newBookingRequest) {
        return Mono.just(newBookingRequest)
                .map(bookingRequest -> {
                    LocalDate dateFrom = bookingRequest.dateFrom();
                    LocalDate dateTo = bookingRequest.dateTo();
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

                    return bookingRequest;
                });
    }

    private Mono<Booking> updatedBookingWithEmployeeDetails(String apiKeySecret, List<String> roles,
                                                            BookingClosingDetails bookingClosingDetails, Booking existingBooking) {
        return employeeService.findEmployeeById(apiKeySecret, roles, bookingClosingDetails.receptionistEmployeeId())
                .map(employeeResponse -> {
                    existingBooking.setStatus(BookingStatus.CLOSED);
                    existingBooking.setReturnBranchId(MongoUtil.getObjectId(employeeResponse.workingBranchId()));

                    return existingBooking;
                });
    }

    private Mono<BookingResponse> updateExistingBooking(String apiKey, List<String> roles, BookingRequest updatedBookingRequest, Booking existingBooking) {
        return getCarIfIsChanged(apiKey, roles, updatedBookingRequest, existingBooking)
                .map(carResponse -> updateExistingBookingWithNewCarDetails(updatedBookingRequest, existingBooking, carResponse))
                .switchIfEmpty(Mono.defer(() -> Mono.just(updateExistingBooking(updatedBookingRequest, existingBooking))))
                .flatMap(updatedExistingBooking -> processBooking(apiKey, roles, existingBooking, updatedExistingBooking));
    }

    private Mono<BookingResponse> processBooking(String apiKey, List<String> roles, Booking existingBooking,
                                                 Booking updatedExistingBooking) {
        return outboxService.saveBookingAndOutbox(updatedExistingBooking, Outbox.Operation.UPDATE)
                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                .delayUntil(savedBookingResponse -> changeCarsStatusIfNeeded(apiKey, roles, savedBookingResponse, existingBooking));
    }

    private Mono<Void> updateCarWhenBookingIsClosed(String apiKey, List<String> roles,
                                                    BookingResponse bookingResponse, BookingClosingDetails bookingClosingDetails) {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(bookingResponse.carId())
                .receptionistEmployeeId(bookingClosingDetails.receptionistEmployeeId())
                .carState(bookingClosingDetails.carState())
                .build();

        return carService.updateCarWhenBookingIsFinished(apiKey, roles, carUpdateDetails);
    }

    private Mono<Void> setCarForNewBooking(String apiKey, List<String> roles, BookingResponse bookingResponse) {
        return carService.changeCarStatus(apiKey, roles, bookingResponse.carId(), CarState.NOT_AVAILABLE);
    }

    private Mono<CarResponse> getCarIfIsChanged(String apiKey, List<String> roles, BookingRequest updatedBookingRequest,
                                                Booking existingBooking) {
        return Mono.just(updatedBookingRequest.carId())
                .filter(id -> !existingBooking.getCarId().toString().equals(id))
                .flatMap(newCarId -> carService.findAvailableCarById(apiKey, roles, newCarId))
                .flatMap(carResponse -> checkIfCarIsFromRightBranch(updatedBookingRequest, carResponse))
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
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Booking with id " + id + " does not exist")));
    }

    private Booking setupNewBooking(BookingRequest newBookingRequest, CarResponse carResponse) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(newBooking.getCustomerUsername());
        newBooking.setCustomerEmail(newBookingRequest.customerEmail());
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

    private Mono<Void> changeCarsStatusIfNeeded(String apiKey, List<String> roles,
                                                BookingResponse updatedBookingResponse, Booking existingBooking) {
        return Mono.just(existingBooking.getCarId().toString())
                .filter(existingBookingCarId -> !existingBookingCarId.equals(updatedBookingResponse.carId()))
                .flatMap(existingBookingCarId -> {
                    List<UpdateCarRequest> updateCarRequests =
                            getUpdateCarRequest(existingBookingCarId, updatedBookingResponse.carId());

                    return carService.updateCarsStatus(apiKey, roles, updateCarRequests);
                });
    }

    private List<UpdateCarRequest> getUpdateCarRequest(String existingCarId, String newCarId) {
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

    private Mono<CarResponse> checkIfCarIsFromRightBranch(BookingRequest updatedBookingRequest, CarResponse carResponse) {
        if (updatedBookingRequest.rentalBranchId().equals(carResponse.actualBranchId())) {
            return Mono.just(carResponse);
        }

        return Mono.error(
                new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot choose car from other branch than selected one"
                )
        );
    }

}
