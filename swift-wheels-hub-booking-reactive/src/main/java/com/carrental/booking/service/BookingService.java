package com.carrental.booking.service;

import com.carrental.booking.mapper.BookingMapper;
import com.carrental.booking.model.Outbox;
import com.carrental.booking.repository.BookingRepository;
import com.carrental.dto.*;
import com.swiftwheelshub.lib.aspect.LogActivity;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Booking;
import com.swiftwheelshub.model.BookingStatus;
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
import java.util.Optional;

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

    public Flux<BookingDto> findAllBookings() {
        return bookingRepository.findAll()
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding bookings: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BookingDto> findBookingById(String id) {
        return findEntityById(id)
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding booking by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<BookingDto> findBookingsByDateOfBooking(String dateOfBooking) {
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

    public Flux<BookingDto> findBookingsByLoggedInUser(String username) {
        return bookingRepository.findByCustomerUsername(username)
                .map(bookingMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding bookings by logged in customer: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Double> getAmountSpentByLoggedInUser(String username) {
        return findBookingsByLoggedInUser(username)
                .map(BookingDto::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .reduce(0D, Double::sum);
    }

    public Mono<Double> getSumOfAllBookingAmount() {
        return findAllBookings()
                .map(BookingDto::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .reduce(0D, Double::sum);
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
            sentParameters = "newBookingDto"
    )
    public Mono<BookingDto> saveBooking(String apiKeyToken, BookingDto newBookingDto) {
        return validateBookingDates(newBookingDto)
                .flatMap(bookingDto -> carService.findAvailableCarById(apiKeyToken, bookingDto.getCarId()))
                .map(carDto -> setupNewBooking(newBookingDto, carDto))
                .flatMap(booking -> outboxService.saveBookingAndOutboxTransactional(booking, Outbox.Operation.CREATE))
                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                .flatMap(bookingDto -> setCarForNewBooking(apiKeyToken, bookingDto))
                .onErrorResume(e -> {
                    log.error("Error while saving booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "Booking update",
            sentParameters = "id"
    )
    public Mono<BookingDto> updateBooking(String apiKeyToken, String id, BookingDto updatedBookingDto) {
        return validateBookingDates(updatedBookingDto)
                .flatMap(bookingDto -> findEntityById(id))
                .flatMap(existingBooking -> getCarIfIsChanged(apiKeyToken, updatedBookingDto, existingBooking)
                        .map(carDto -> updateExistingBookingWithNewCarDetails(updatedBookingDto, existingBooking, carDto))
                        .switchIfEmpty(Mono.defer(() -> Mono.just(updateExistingBooking(updatedBookingDto, existingBooking))))
                        .flatMap(updatedExistingBooking -> outboxService.saveBookingAndOutboxTransactional(updatedExistingBooking, Outbox.Operation.UPDATE)
                                .map(outbox -> bookingMapper.mapEntityToDto(outbox.getContent()))
                                .flatMap(savedBookingDto -> changeCarsStatus(apiKeyToken, savedBookingDto, existingBooking))))
                .onErrorResume(e -> {
                    log.error("Error while updating booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<BookingDto> closeBooking(String apiKeyToken, BookingClosingDetailsDto bookingUpdateDetailsDto) {
        return findEntityById(bookingUpdateDetailsDto.getBookingId())
                .flatMap(existingBooking -> employeeService.findEmployeeById(apiKeyToken, bookingUpdateDetailsDto.getReceptionistEmployeeId())
                        .map(employeeDto -> {
                            existingBooking.setStatus(BookingStatus.CLOSED);
                            existingBooking.setReturnBranchId(MongoUtil.getObjectId(employeeDto.getWorkingBranchId()));

                            return existingBooking;
                        }))
                .flatMap(bookingRepository::save)
                .map(bookingMapper::mapEntityToDto)
                .flatMap(bookingDto -> updateCarWhenBookingIsClosed(apiKeyToken, bookingDto, bookingUpdateDetailsDto))
                .onErrorResume(e -> {
                    log.error("Error while closing booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "Booking deletion",
            sentParameters = "id"
    )
    public Mono<Void> deleteBookingById(String apiKeyToken, String id) {
        return findEntityById(id)
                .flatMap(booking -> outboxService.processBookingDeletion(booking, Outbox.Operation.DELETE))
                .flatMap(booking -> carService.changeCarStatus(apiKeyToken, booking.getCarId().toString(), CarStatusEnum.AVAILABLE))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while deleting booking by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Void> processBookingDeletion(String username) {
        return bookingRepository.deleteByCustomerUsername(username);
    }

    private Mono<BookingDto> validateBookingDates(BookingDto newBookingDto) {
        return Mono.just(newBookingDto)
                .flatMap(bookingDto -> {
                    LocalDate dateFrom = newBookingDto.getDateFrom();
                    LocalDate dateTo = newBookingDto.getDateTo();
                    LocalDate currentDate = LocalDate.now();

                    LocalDate newDateFrom = Optional.ofNullable(dateFrom)
                            .orElseThrow(() -> new SwiftWheelsHubException("Date from is null"));
                    LocalDate newDateTo = Optional.ofNullable(dateTo)
                            .orElseThrow(() -> new SwiftWheelsHubException("Date to is null"));

                    if (newDateFrom.isBefore(currentDate) || newDateTo.isBefore(currentDate)) {
                        return Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "A date of booking cannot be in the past"
                                )
                        );
                    }

                    if (dateFrom.isAfter(dateTo)) {
                        return Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Date from is after date to"
                                )
                        );
                    }

                    return Mono.just(newBookingDto);
                });
    }

    private Mono<BookingDto> updateCarWhenBookingIsClosed(String apiKeyToken, BookingDto bookingDto,
                                                          BookingClosingDetailsDto bookingClosingDetailsDto) {
        CarDetailsForUpdateDto carDetailsForUpdateDto = new CarDetailsForUpdateDto()
                .carId(bookingDto.getCarId())
                .receptionistEmployeeId(bookingClosingDetailsDto.getReceptionistEmployeeId())
                .carStatus(bookingClosingDetailsDto.getCarStatus());

        return carService.updateCarWhenBookingIsFinished(apiKeyToken, carDetailsForUpdateDto)
                .map(carDto -> bookingDto);
    }

    private Mono<BookingDto> setCarForNewBooking(String apiKeyToken, BookingDto bookingDto) {
        return carService.changeCarStatus(apiKeyToken, bookingDto.getCarId(), CarStatusEnum.NOT_AVAILABLE)
                .map(carDto -> bookingDto);
    }

    private Mono<CarDto> getCarIfIsChanged(String apiKeyToken, BookingDto updatedBookingDto,
                                           Booking existingBooking) {
        return Mono.just(Optional.ofNullable(updatedBookingDto.getCarId()).orElseThrow())
                .filter(id -> !existingBooking.getCarId().toString().equals(id))
                .flatMap(newCarId -> carService.findAvailableCarById(apiKeyToken, newCarId))
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

    private Booking setupNewBooking(BookingDto newBookingDto, CarDto carDto) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingDto);
        double amount = Optional.ofNullable((carDto.getAmount())).orElseThrow().doubleValue();

        newBooking.setCustomerUsername(newBooking.getCustomerUsername());
        newBooking.setCustomerEmail(newBookingDto.getCustomerEmail());
        newBooking.setCarId(MongoUtil.getObjectId(Optional.ofNullable(carDto.getId()).orElseThrow()));
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(MongoUtil.getObjectId(Optional.ofNullable(carDto.getActualBranchId()).orElseThrow()));
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBooking.getDateFrom(), newBooking.getDateTo(), amount));
        newBooking.setRentalCarPrice(Optional.ofNullable(carDto.getAmount()).orElseThrow().doubleValue());

        return newBooking;
    }

    private Booking updateExistingBooking(BookingDto updatedBookingDto, Booking existingBooking) {
        LocalDate dateFrom = updatedBookingDto.getDateFrom();
        LocalDate dateTo = updatedBookingDto.getDateTo();

        existingBooking.setDateFrom(dateFrom);
        existingBooking.setDateTo(dateTo);
        existingBooking.setAmount(getAmount(dateFrom, dateTo, existingBooking.getRentalCarPrice()));

        return existingBooking;
    }

    private Booking updateExistingBookingWithNewCarDetails(BookingDto updatedBookingDto, Booking existingBooking,
                                                           CarDto carDto) {
        LocalDate dateFrom = updatedBookingDto.getDateFrom();
        LocalDate dateTo = updatedBookingDto.getDateTo();

        Booking updatedExistingBooking = bookingMapper.getNewBooking(existingBooking);
        double amount = Optional.ofNullable(carDto.getAmount()).orElseThrow().doubleValue();

        updatedExistingBooking.setDateFrom(dateFrom);
        updatedExistingBooking.setDateTo(dateTo);
        updatedExistingBooking.setCarId(MongoUtil.getObjectId(carDto.getId()));
        updatedExistingBooking.setRentalBranchId(MongoUtil.getObjectId(carDto.getActualBranchId()));
        updatedExistingBooking.setAmount(getAmount(dateFrom, dateTo, amount));
        updatedExistingBooking.setRentalCarPrice(amount);

        return updatedExistingBooking;
    }

    private Mono<BookingDto> changeCarsStatus(String apiKeyToken, BookingDto updatedBookingDto,
                                              Booking existingBooking) {
        return Mono.just(existingBooking.getCarId().toString())
                .filter(existingBookingCarId -> !existingBookingCarId.equals(updatedBookingDto.getCarId()))
                .flatMap(existingBookingCarId -> {
                    List<CarDetailsForUpdateDto> carsForStatusUpdate =
                            getCarsForStatusUpdate(existingBookingCarId, updatedBookingDto.getCarId());

                    return carService.updateCarsStatus(apiKeyToken, carsForStatusUpdate)
                            .collectList()
                            .map(carDtoList -> updatedBookingDto);
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(updatedBookingDto)));
    }

    private List<CarDetailsForUpdateDto> getCarsForStatusUpdate(String existingCarId, String newCarId) {
        return List.of(
                new CarDetailsForUpdateDto().carId(existingCarId).carStatus(CarStatusEnum.AVAILABLE),
                new CarDetailsForUpdateDto().carId(newCarId).carStatus(CarStatusEnum.NOT_AVAILABLE)
        );
    }

    private Double getAmount(LocalDate dateFrom, LocalDate dateTo, Double amount) {
        int bookingDays = Period.between(dateFrom, dateTo).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return bookingDays * amount;
    }

}
