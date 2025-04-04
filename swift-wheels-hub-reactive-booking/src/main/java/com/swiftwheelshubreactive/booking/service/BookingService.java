package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.producer.CreateBookingCarUpdateProducerService;
import com.swiftwheelshubreactive.booking.producer.UpdateBookingUpdateCarsProducerService;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.AvailableCarInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarStatusUpdate;
import com.swiftwheelshubreactive.dto.UpdateCarsRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.aspect.LogActivity;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingProcessStatus;
import com.swiftwheelshubreactive.model.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_OF_BOOKING = "dateOfBooking";
    private static final String LOCKED = "locked";
    private final BookingRepository bookingRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final CreateBookingCarUpdateProducerService createBookingCarUpdateProducerService;
    private final UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;
    private final CarService carService;
    private final OutboxService outboxService;
    private final ReactiveRedisOperations<String, String> redisOperations;
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
    public Mono<BookingResponse> saveBooking(AuthenticationInfo authenticationInfo, BookingRequest newBookingRequest) {
        return validateBookingDates(newBookingRequest)
                .flatMap(bookingRequest -> lockCar(bookingRequest.carId()))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Car is not available")))
                .flatMap(_ -> createNewBooking(authenticationInfo, newBookingRequest))
                .flatMap(bookingRepository::save)
                .flatMap(this::processCreatedBooking)
                .delayUntil(booking -> unlockCar(booking.getActualCarId().toString()))
                .map(bookingMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @LogActivity(
            activityDescription = "Booking update",
            sentParameters = "id"
    )
    public Mono<BookingResponse> updateBooking(AuthenticationInfo authenticationInfo,
                                               String id,
                                               BookingRequest updatedBookingRequest) {
        return validateBookingDates(updatedBookingRequest)
                .flatMap(_ -> findEntityById(id))
                .flatMap(existingBooking -> processUpdatedBooking(authenticationInfo, updatedBookingRequest, existingBooking))
                .onErrorMap(e -> {
                    log.error("Error while updating booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> closeBooking(BookingClosingDetails bookingClosingDetails) {
        return updatedBookingWithClosingDetails(bookingClosingDetails)
                .flatMap(bookingRepository::save)
                .then()
                .onErrorResume(e -> {
                    log.error("Error while closing booking: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @LogActivity(
            activityDescription = "Booking deletion",
            sentParameters = "id"
    )
    public Mono<Void> deleteBookingByCustomerUsername(String username) {
        return bookingRepository.existsByCustomerUsernameAndStatus(username, BookingStatus.IN_PROGRESS)
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("There are bookings in progress")))
                .flatMapMany(_ -> bookingRepository.findByCustomerUsername(username))
                .flatMap(booking -> outboxService.processBookingDeletion(booking, Outbox.Operation.DELETE))
                .then()
                .onErrorMap(e -> {
                    log.error("Error while deleting booking by username: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Mono<BookingRequest> validateBookingDates(BookingRequest newBookingRequest) {
        return Mono.just(newBookingRequest)
                .handle((bookingRequest, sink) -> {
                    LocalDate dateFrom = bookingRequest.dateFrom();
                    LocalDate dateTo = bookingRequest.dateTo();
                    LocalDate currentDate = LocalDate.now();

                    if (dateFrom.isBefore(currentDate) || dateTo.isBefore(currentDate)) {
                        sink.error(new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "A date of booking cannot be in the past"
                        ));

                        return;
                    }

                    if (dateFrom.isAfter(dateTo)) {
                        sink.error(new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Date from is after date to"
                        ));

                        return;
                    }

                    sink.next(bookingRequest);
                });
    }

    private Mono<Boolean> lockCar(String carId) {
        return redisOperations.opsForValue().setIfAbsent(carId, LOCKED, Duration.ofSeconds(30));
    }

    private Mono<Boolean> unlockCar(String carId) {
        return redisOperations.opsForValue().delete(carId);
    }

    private Mono<Booking> createNewBooking(AuthenticationInfo authenticationInfo,
                                           BookingRequest newBookingRequest) {
        return carService.findAvailableCarById(authenticationInfo, newBookingRequest.carId())
                .map(availableCarInfo -> setupNewBooking(newBookingRequest, availableCarInfo, authenticationInfo));
    }

    private Mono<Booking> processCreatedBooking(Booking pendingBooking) {
        return updateCarForNewBooking(pendingBooking)
                .filter(Boolean.TRUE::equals)
                .map(_ -> bookingMapper.getSuccessfulCreatedBooking(pendingBooking))
                .flatMap(booking -> outboxService.processBookingSaving(booking, Outbox.Operation.CREATE))
                .switchIfEmpty(saveBookingAfterFailedCarServiceResponse(bookingMapper.getFailedCreatedBooking(pendingBooking)));
    }

    private Mono<Booking> updatedBookingWithClosingDetails(BookingClosingDetails bookingClosingDetails) {
        return findEntityById(bookingClosingDetails.bookingId())
                .map(existingBooking -> {
                    Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);

                    updatedBooking.setStatus(BookingStatus.CLOSED);
                    updatedBooking.setReturnBranchId(MongoUtil.getObjectId(bookingClosingDetails.returnBranchId()));
                    updatedBooking.setBookingProcessStatus(BookingProcessStatus.SAVED_CLOSED_BOOKING);

                    return updatedBooking;
                });
    }

    private Mono<BookingResponse> processUpdatedBooking(AuthenticationInfo authenticationInfo,
                                                        BookingRequest updatedBookingRequest,
                                                        Booking existingBooking) {
        return getNewCarIfChanged(authenticationInfo, updatedBookingRequest, existingBooking)
                .flatMap(availableCarInfo -> processNewBookingData(updatedBookingRequest, existingBooking, availableCarInfo))
                .flatMap(pendingUpdatedBooking -> handleBookingWhenCarIsChanged(existingBooking, pendingUpdatedBooking))
                .switchIfEmpty(handleBookingWhenCarIsNotChanged(updatedBookingRequest, existingBooking))
                .map(bookingMapper::mapEntityToDto);
    }

    private Mono<AvailableCarInfo> getNewCarIfChanged(AuthenticationInfo authenticationInfo,
                                                      BookingRequest updatedBookingRequest,
                                                      Booking existingBooking) {
        return Mono.just(updatedBookingRequest.carId())
                .filter(carId -> isCarChanged(existingBooking.getActualCarId().toString(), carId))
                .flatMap(newCarId -> carService.findAvailableCarById(authenticationInfo, newCarId))
                .flatMap(availableCarInfo -> checkIfCarIsFromRightBranch(updatedBookingRequest, availableCarInfo))
                .switchIfEmpty(Mono.empty());
    }

    private boolean isCarChanged(String existingBookingId, String newCarId) {
        return !existingBookingId.equals(newCarId);
    }

    private Mono<Booking> processNewBookingData(BookingRequest updatedBookingRequest, Booking existingBooking, AvailableCarInfo availableCarInfo) {
        return lockCar(availableCarInfo.id())
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Car is not available")))
                .map(_ -> updateBookingWithNewData(updatedBookingRequest, existingBooking, availableCarInfo));
    }

    private Mono<Booking> handleBookingWhenCarIsChanged(Booking existingBooking, Booking pendingUpdatedBooking) {
        return bookingRepository.save(pendingUpdatedBooking)
                .flatMap(savedUpdatedBooking -> changeCarsStatuses(savedUpdatedBooking, existingBooking))
                .filter(Boolean.TRUE::equals)
                .map(_ -> bookingMapper.getSuccessfulUpdatedBooking(pendingUpdatedBooking))
                .flatMap(updatedBooking -> outboxService.processBookingSaving(updatedBooking, Outbox.Operation.UPDATE))
                .switchIfEmpty(saveBookingAfterFailedCarServiceResponse(bookingMapper.getFailedUpdatedBooking(pendingUpdatedBooking)))
                .delayUntil(booking -> unlockCar(booking.getActualCarId().toString()));
    }

    private Mono<Booking> handleBookingWhenCarIsNotChanged(BookingRequest updatedBookingRequest, Booking existingBooking) {
        return Mono.defer(
                () -> Mono.just(getUpdatedExistingBooking(updatedBookingRequest, existingBooking))
                        .map(bookingMapper::getSuccessfulUpdatedBooking)
                        .flatMap(booking -> outboxService.processBookingSaving(booking, Outbox.Operation.UPDATE))
        );
    }

    private Mono<Booking> saveBookingAfterFailedCarServiceResponse(Booking pendingUpdatedBooking) {
        return Mono.defer(() -> bookingRepository.save(pendingUpdatedBooking));
    }

    private Mono<Boolean> updateCarForNewBooking(Booking booking) {
        return createBookingCarUpdateProducerService.sendCarUpdateDetails(getCarStatusUpdate(booking.getActualCarId().toString()));
    }

    private CarStatusUpdate getCarStatusUpdate(String actualCarId) {
        return CarStatusUpdate.builder()
                .carId(actualCarId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
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

    private Booking setupNewBooking(BookingRequest newBookingRequest,
                                    AvailableCarInfo availableCarInfo,
                                    AuthenticationInfo authenticationInfo) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = availableCarInfo.amount();

        newBooking.setCustomerUsername(authenticationInfo.username());
        newBooking.setCustomerEmail(authenticationInfo.email());
        newBooking.setActualCarId(MongoUtil.getObjectId(availableCarInfo.id()));
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(MongoUtil.getObjectId(availableCarInfo.actualBranchId()));
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setRentalCarPrice(amount);
        newBooking.setBookingProcessStatus(BookingProcessStatus.IN_CREATION);

        return newBooking;
    }

    private Booking getUpdatedExistingBooking(BookingRequest updatedBookingRequest, Booking existingBooking) {
        LocalDate dateFrom = updatedBookingRequest.dateFrom();
        LocalDate dateTo = updatedBookingRequest.dateTo();

        Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);

        updatedBooking.setDateFrom(dateFrom);
        updatedBooking.setDateTo(dateTo);

        return updatedBooking;
    }

    private Booking updateBookingWithNewData(BookingRequest updatedBookingRequest,
                                             Booking existingBooking,
                                             AvailableCarInfo availableCarInfo) {
        LocalDate dateFrom = updatedBookingRequest.dateFrom();
        LocalDate dateTo = updatedBookingRequest.dateTo();

        final ObjectId existingCarId = existingBooking.getActualCarId();
        Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);
        BigDecimal amount = availableCarInfo.amount();

        updatedBooking.setDateFrom(dateFrom);
        updatedBooking.setDateTo(dateTo);
        updatedBooking.setActualCarId(MongoUtil.getObjectId(availableCarInfo.id()));
        updatedBooking.setPreviousCarId(existingCarId);
        updatedBooking.setRentalBranchId(MongoUtil.getObjectId(availableCarInfo.actualBranchId()));
        updatedBooking.setRentalCarPrice(amount);
        updatedBooking.setBookingProcessStatus(BookingProcessStatus.IN_UPDATE);

        return updatedBooking;
    }

    private Mono<Boolean> changeCarsStatuses(Booking updatedBooking, Booking existingBooking) {
        return Mono.just(existingBooking.getActualCarId().toString())
                .flatMap(existingBookingCarId -> {
                    UpdateCarsRequest updateCarsRequest =
                            getUpdateCarsRequest(existingBookingCarId, updatedBooking.getActualCarId().toString());

                    return updateBookingUpdateCarsProducerService.sendUpdateCarsRequest(updateCarsRequest);
                });
    }

    private UpdateCarsRequest getUpdateCarsRequest(String existingCarId, String newCarId) {
        return UpdateCarsRequest.builder()
                .previousCarId(existingCarId)
                .actualCarId(newCarId)
                .build();
    }

    private Mono<AvailableCarInfo> checkIfCarIsFromRightBranch(BookingRequest updatedBookingRequest,
                                                               AvailableCarInfo availableCarInfo) {
        if (updatedBookingRequest.rentalBranchId().equals(availableCarInfo.actualBranchId())) {
            return Mono.just(availableCarInfo);
        }

        return Mono.error(
                new SwiftWheelsHubResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot choose car from other branch than selected one"
                )
        );
    }

}
