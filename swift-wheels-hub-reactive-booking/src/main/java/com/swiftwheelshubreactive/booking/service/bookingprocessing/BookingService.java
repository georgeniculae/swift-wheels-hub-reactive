package com.swiftwheelshubreactive.booking.service.bookingprocessing;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.booking.service.outbox.CreatedOutboxService;
import com.swiftwheelshubreactive.booking.service.outbox.DeletedOutboxService;
import com.swiftwheelshubreactive.booking.service.outbox.UpdatedOutboxService;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.AvailableCarInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.aspect.LogActivity;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Booking;
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
    private final CarService carService;
    private final CreatedOutboxService createdOutboxService;
    private final UpdatedOutboxService updatedOutboxService;
    private final DeletedOutboxService deletedOutboxService;
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
    public Mono<Void> saveBooking(AuthenticationInfo authenticationInfo, BookingRequest newBookingRequest) {
        return validateBookingDates(newBookingRequest)
                .flatMap(bookingRequest -> lockCar(bookingRequest.carId()))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Car is not available")))
                .flatMap(_ -> getNewBooking(authenticationInfo, newBookingRequest))
                .flatMap(createdOutboxService::processBookingSave)
                .onErrorMap(e -> {
                    log.error("Error while saving booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @LogActivity(
            activityDescription = "Booking update",
            sentParameters = "id"
    )
    public Mono<Void> updateBooking(AuthenticationInfo authenticationInfo,
                                    String id,
                                    BookingRequest updatedBookingRequest) {
        return validateBookingDates(updatedBookingRequest)
                .flatMap(_ -> findEntityById(id))
                .flatMap(existingBooking -> processBookingUpdate(authenticationInfo, updatedBookingRequest, existingBooking))
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
                .flatMap(deletedOutboxService::processBookingDeletion)
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
                        sink.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "A date of booking cannot be in the past"
                                )
                        );

                        return;
                    }

                    if (dateFrom.isAfter(dateTo)) {
                        sink.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Date from is after date to"
                                )
                        );

                        return;
                    }

                    sink.next(bookingRequest);
                });
    }

    private Mono<Boolean> lockCar(String carId) {
        return redisOperations.opsForValue().setIfAbsent(carId, LOCKED, Duration.ofSeconds(30));
    }

    private Mono<Booking> getNewBooking(AuthenticationInfo authenticationInfo,
                                        BookingRequest newBookingRequest) {
        return carService.findAvailableCarById(authenticationInfo, newBookingRequest.carId())
                .map(availableCarInfo -> setupNewBooking(newBookingRequest, availableCarInfo, authenticationInfo));
    }

    private Mono<Booking> updatedBookingWithClosingDetails(BookingClosingDetails bookingClosingDetails) {
        return findEntityById(bookingClosingDetails.bookingId())
                .map(existingBooking -> {
                    Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);

                    updatedBooking.setStatus(BookingStatus.CLOSED);
                    updatedBooking.setReturnBranchId(MongoUtil.getObjectId(bookingClosingDetails.returnBranchId()));

                    return updatedBooking;
                });
    }

    private Mono<Void> processBookingUpdate(AuthenticationInfo authenticationInfo,
                                            BookingRequest updatedBookingRequest,
                                            Booking existingBooking) {
        return getNewCarIfChanged(authenticationInfo, updatedBookingRequest, existingBooking)
                .flatMap(availableCarInfo -> processNewBookingData(updatedBookingRequest, existingBooking, availableCarInfo))
                .flatMap(this::handleBookingWhenCarIsChanged)
                .switchIfEmpty(handleBookingWhenCarIsNotChanged(updatedBookingRequest, existingBooking))
                .then();
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

    private Mono<Booking> handleBookingWhenCarIsChanged(Booking pendingUpdatedBooking) {
        return updatedOutboxService.processBookingUpdate(pendingUpdatedBooking);
    }

    private Mono<Booking> handleBookingWhenCarIsNotChanged(BookingRequest updatedBookingRequest, Booking existingBooking) {
        return Mono.defer(() -> processExistingBooking(updatedBookingRequest, existingBooking));
    }

    private Mono<Booking> processExistingBooking(BookingRequest updatedBookingRequest, Booking existingBooking) {
        return Mono.just(getUpdatedExistingBooking(updatedBookingRequest, existingBooking))
                .flatMap(updatedOutboxService::processBookingUpdate);
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

        return updatedBooking;
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
