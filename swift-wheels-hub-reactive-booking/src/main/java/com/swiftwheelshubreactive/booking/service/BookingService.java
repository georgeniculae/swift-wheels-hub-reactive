package com.swiftwheelshubreactive.booking.service;

import com.swiftwheelshubreactive.booking.mapper.BookingMapper;
import com.swiftwheelshubreactive.booking.model.Outbox;
import com.swiftwheelshubreactive.booking.repository.BookingRepository;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.dto.UserInfo;
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
    private final CustomerService customerService;
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
    public Mono<BookingResponse> saveBooking(AuthenticationInfo authenticationInfo, BookingRequest newBookingRequest) {
        return validateBookingDates(newBookingRequest)
                .flatMap(bookingRequest -> createNewBooking(authenticationInfo, newBookingRequest, bookingRequest))
                .flatMap(bookingRepository::save)
                .flatMap(pendingBooking -> processCreatedBooking(authenticationInfo, pendingBooking))
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

    public Mono<BookingResponse> closeBooking(AuthenticationInfo authenticationInfo, BookingClosingDetails bookingClosingDetails) {
        return updatedBookingWithEmployeeDetails(authenticationInfo, bookingClosingDetails)
                .flatMap(bookingRepository::save)
                .flatMap(pendingSavedBooking -> processBookingIfCarIsUpdated(authenticationInfo, bookingClosingDetails, pendingSavedBooking))
                .map(bookingMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while closing booking: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    @LogActivity(
            activityDescription = "Booking deletion",
            sentParameters = "id"
    )
    public Mono<Void> deleteBookingByCustomerUsername(String username) {
        return bookingRepository.findByCustomerUsername(username)
                .collectList()

                .filter(this::checkIfThereIsNoBookingInProgress)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("There are bookings in progress")))
                .flatMap(bookings -> outboxService.processBookingDeletion(bookings, Outbox.Operation.DELETE))
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

    private Mono<Booking> createNewBooking(AuthenticationInfo authenticationInfo, BookingRequest newBookingRequest, BookingRequest bookingRequest) {
        return Mono.zip(
                carService.findAvailableCarById(authenticationInfo, bookingRequest.carId()),
                customerService.findUserByUsername(authenticationInfo),
                (carResponse, userInfo) -> setupNewBooking(newBookingRequest, carResponse, userInfo)
        );
    }

    private Mono<Booking> processCreatedBooking(AuthenticationInfo authenticationInfo, Booking pendingBooking) {
        return updateCarForNewBooking(authenticationInfo, pendingBooking)
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .map(_ -> bookingMapper.getSuccessfulCreatedBooking(pendingBooking))
                .flatMap(booking -> outboxService.processBookingSaving(booking, Outbox.Operation.CREATE))
                .switchIfEmpty(saveBookingAfterFailedCarServiceResponse(bookingMapper.getFailedCreatedBooking(pendingBooking)));
    }

    private Mono<Booking> updatedBookingWithEmployeeDetails(AuthenticationInfo authenticationInfo,
                                                            BookingClosingDetails bookingClosingDetails) {
        return Mono.zip(
                findEntityById(bookingClosingDetails.bookingId()),
                employeeService.findEmployeeById(authenticationInfo, bookingClosingDetails.receptionistEmployeeId()),
                (existingBooking, employeeResponse) -> {
                    Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);

                    updatedBooking.setStatus(BookingStatus.CLOSED);
                    updatedBooking.setReturnBranchId(MongoUtil.getObjectId(employeeResponse.workingBranchId()));
                    updatedBooking.setBookingProcessStatus(BookingProcessStatus.IN_CLOSING);

                    return updatedBooking;
                });
    }

    private Mono<BookingResponse> processUpdatedBooking(AuthenticationInfo authenticationInfo,
                                                        BookingRequest updatedBookingRequest,
                                                        Booking existingBooking) {
        return getCarIfIsChanged(authenticationInfo, updatedBookingRequest, existingBooking)
                .map(carResponse -> updateBookingWithNewData(updatedBookingRequest, existingBooking, carResponse))
                .flatMap(pendingUpdatedBooking -> handleBookingWhenCarIsChanged(authenticationInfo, existingBooking, pendingUpdatedBooking))
                .switchIfEmpty(handleBookingWhenCarIsNotChanged(updatedBookingRequest, existingBooking))
                .map(bookingMapper::mapEntityToDto);
    }

    private Mono<CarResponse> getCarIfIsChanged(AuthenticationInfo authenticationInfo,
                                                BookingRequest updatedBookingRequest,
                                                Booking existingBooking) {
        return Mono.just(updatedBookingRequest.carId())
                .filter(carId -> isCarChanged(existingBooking.getCarId().toString(), carId))
                .flatMap(newCarId -> carService.findAvailableCarById(authenticationInfo, newCarId))
                .flatMap(carResponse -> checkIfCarIsFromRightBranch(updatedBookingRequest, carResponse))
                .switchIfEmpty(Mono.empty());
    }

    private boolean isCarChanged(String existingBookingId, String newCarId) {
        return !existingBookingId.equals(newCarId);
    }

    private Mono<Booking> handleBookingWhenCarIsChanged(AuthenticationInfo authenticationInfo, Booking existingBooking, Booking pendingUpdatedBooking) {
        return bookingRepository.save(pendingUpdatedBooking)
                .flatMap(savedUpdatedBooking -> changeCarsStatuses(authenticationInfo, savedUpdatedBooking, existingBooking))
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .map(_ -> bookingMapper.getSuccessfulUpdatedBooking(pendingUpdatedBooking))
                .flatMap(updatedBooking -> outboxService.processBookingSaving(updatedBooking, Outbox.Operation.UPDATE))
                .switchIfEmpty(saveBookingAfterFailedCarServiceResponse(bookingMapper.getFailedUpdatedBooking(pendingUpdatedBooking)));
    }

    private Mono<Booking> handleBookingWhenCarIsNotChanged(BookingRequest updatedBookingRequest, Booking existingBooking) {
        return Mono.defer(
                () -> Mono.just(getUpdatedExistingBooking(updatedBookingRequest, existingBooking))
                        .map(bookingMapper::getSuccessfulUpdatedBooking)
                        .flatMap(booking -> outboxService.processBookingSaving(booking, Outbox.Operation.UPDATE))
        );
    }

    private Mono<Booking> processBookingIfCarIsUpdated(AuthenticationInfo authenticationInfo,
                                                       BookingClosingDetails bookingClosingDetails,
                                                       Booking pendingSavedBooking) {
        return updateCarWhenBookingIsClosed(authenticationInfo, pendingSavedBooking, bookingClosingDetails)
                .filter(StatusUpdateResponse::isUpdateSuccessful)
                .flatMap(_ -> bookingRepository.save(bookingMapper.getSuccessfulClosedBooking(pendingSavedBooking)))
                .switchIfEmpty(saveBookingAfterFailedCarServiceResponse(bookingMapper.getFailedClosedBooking(pendingSavedBooking)));
    }

    private Mono<StatusUpdateResponse> updateCarWhenBookingIsClosed(AuthenticationInfo authenticationInfo,
                                                                    Booking bookingResponse,
                                                                    BookingClosingDetails bookingClosingDetails) {
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(bookingResponse.getCarId().toString())
                .receptionistEmployeeId(bookingClosingDetails.receptionistEmployeeId())
                .carState(bookingClosingDetails.carState())
                .build();

        return carService.updateCarWhenBookingIsFinished(authenticationInfo, carUpdateDetails);
    }

    private Mono<Booking> saveBookingAfterFailedCarServiceResponse(Booking pendingUpdatedBooking) {
        return Mono.defer(() -> bookingRepository.save(pendingUpdatedBooking));
    }

    private Mono<StatusUpdateResponse> updateCarForNewBooking(AuthenticationInfo authenticationInfo, Booking booking) {
        return carService.changeCarStatus(authenticationInfo, booking.getCarId().toString(), CarState.NOT_AVAILABLE);
    }

    private boolean checkIfThereIsNoBookingInProgress(List<Booking> bookings) {
        return bookings.stream().noneMatch(booking -> BookingStatus.IN_PROGRESS == booking.getStatus());
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

    private Booking setupNewBooking(BookingRequest newBookingRequest, CarResponse carResponse, UserInfo userInfo) {
        Booking newBooking = bookingMapper.mapDtoToEntity(newBookingRequest);
        BigDecimal amount = carResponse.amount();

        newBooking.setCustomerUsername(userInfo.username());
        newBooking.setCustomerEmail(userInfo.email());
        newBooking.setCarId(MongoUtil.getObjectId(carResponse.id()));
        newBooking.setDateOfBooking(LocalDate.now());
        newBooking.setRentalBranchId(MongoUtil.getObjectId(carResponse.actualBranchId()));
        newBooking.setStatus(BookingStatus.IN_PROGRESS);
        newBooking.setAmount(getAmount(newBooking.getDateFrom(), newBooking.getDateTo(), amount));
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
        updatedBooking.setAmount(getAmount(dateFrom, dateTo, existingBooking.getRentalCarPrice()));

        return updatedBooking;
    }

    private Booking updateBookingWithNewData(BookingRequest updatedBookingRequest,
                                             Booking existingBooking,
                                             CarResponse carResponse) {
        LocalDate dateFrom = updatedBookingRequest.dateFrom();
        LocalDate dateTo = updatedBookingRequest.dateTo();

        Booking updatedBooking = bookingMapper.getNewBookingInstance(existingBooking);
        BigDecimal amount = carResponse.amount();

        updatedBooking.setDateFrom(dateFrom);
        updatedBooking.setDateTo(dateTo);
        updatedBooking.setCarId(MongoUtil.getObjectId(carResponse.id()));
        updatedBooking.setRentalBranchId(MongoUtil.getObjectId(carResponse.actualBranchId()));
        updatedBooking.setAmount(getAmount(dateFrom, dateTo, amount));
        updatedBooking.setRentalCarPrice(amount);
        updatedBooking.setBookingProcessStatus(BookingProcessStatus.IN_UPDATE);

        return updatedBooking;
    }

    private Mono<StatusUpdateResponse> changeCarsStatuses(AuthenticationInfo authenticationInfo,
                                                          Booking updatedBooking,
                                                          Booking existingBooking) {
        return Mono.just(existingBooking.getCarId().toString())
                .flatMap(existingBookingCarId -> {
                    List<UpdateCarRequest> updateCarRequests =
                            getUpdateCarRequestList(existingBookingCarId, updatedBooking.getCarId().toString());

                    return carService.updateCarsStatus(authenticationInfo, updateCarRequests);
                });
    }

    private List<UpdateCarRequest> getUpdateCarRequestList(String existingCarId, String newCarId) {
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
