package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.BookingUpdateResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.lib.aspect.LogActivity;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Invoice;
import com.swiftwheelshubreactive.model.InvoiceProcessStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private static final String TOTAL_AMOUNT = "totalAmount";
    private final InvoiceRepository invoiceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RevenueService revenueService;
    private final BookingService bookingService;
    private final CarService carService;
    private final InvoiceMapper invoiceMapper;

    public Flux<InvoiceResponse> findAllInvoices() {
        return invoiceRepository.findAll()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all invoices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<InvoiceResponse> findAllActiveInvoices() {
        return findActiveInvoices()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all active invoices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<InvoiceResponse> findAllInvoicesByCustomerUsername(String customerUsername) {
        return invoiceRepository.findByCustomerUsername(customerUsername)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding invoices by customer id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<InvoiceResponse> findInvoiceById(String id) {
        return findEntityById(id)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding invoice by id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<InvoiceResponse> findInvoicesByComments(String comments) {
        return invoiceRepository.findByComments(comments)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Invoice with comment: " + comments + " does not exist")))
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding invoices by comments: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Long> countInvoices() {
        return invoiceRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting all invoices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Long> countAllActiveInvoices() {
        return countActiveInvoices()
                .onErrorMap(e -> {
                    log.error("Error while counting all active invoices: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<InvoiceResponse> saveInvoice(BookingResponse newBookingResponse) {
        return invoiceRepository.existsByBookingId(MongoUtil.getObjectId(newBookingResponse.id()))
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invoice already exists"
                                )
                        )
                )
                .flatMap(_ -> invoiceRepository.save(getInvoice(newBookingResponse)))
                .map(invoiceMapper::mapEntityToDto);
    }

    public Mono<InvoiceResponse> updateInvoiceAfterBookingUpdate(BookingResponse newBookingResponse) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(newBookingResponse.id()))
                .flatMap(invoice -> {
                    Invoice updatedInvoice = invoiceMapper.getNewInvoiceInstance(invoice);
                    updatedInvoice.setCarId(MongoUtil.getObjectId(newBookingResponse.carId()));

                    return invoiceRepository.save(updatedInvoice);
                })
                .map(invoiceMapper::mapEntityToDto);
    }

    @LogActivity(
            sentParameters = {"id", "invoiceRequest"},
            activityDescription = "Invoice closing"
    )
    public Mono<InvoiceResponse> closeInvoice(AuthenticationInfo authenticationInfo, String id, InvoiceRequest invoiceRequest) {
        return validateInvoice(invoiceRequest)
                .flatMap(request -> setupExistingInvoice(authenticationInfo, id, request))
                .flatMap(invoiceRepository::save)
                .flatMap(invoice -> processInvoiceClosing(authenticationInfo, invoice))
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while closing invoice: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> deleteInvoiceByBookingId(String bookingId) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(bookingId))
                .flatMap(invoice -> invoiceRepository.deleteById(invoice.getId()));
    }

    private Invoice getInvoice(BookingResponse newBookingResponse) {
        Invoice invoice = new Invoice();

        invoice.setCustomerUsername(newBookingResponse.customerUsername());
        invoice.setCarId(MongoUtil.getObjectId(newBookingResponse.carId()));
        invoice.setBookingId(MongoUtil.getObjectId(newBookingResponse.id()));

        return invoice;
    }

    private Mono<InvoiceRequest> validateInvoice(InvoiceRequest invoiceRequest) {
        return Mono.just(invoiceRequest)
                .handle((request, sink) -> {
                    LocalDate dateOfReturnOfTheCar = Optional.ofNullable((request.carReturnDate()))
                            .orElseThrow(() -> new SwiftWheelsHubException("Car return date is null"));

                    validateDateOfReturnOfTheCar(dateOfReturnOfTheCar);

                    if (Boolean.TRUE.equals(request.isVehicleDamaged()) && ObjectUtils.isEmpty(request.damageCost())) {
                        sink.error(new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "If the vehicle is damaged, the damage cost cannot be null/empty"
                        ));

                        return;
                    }

                    sink.next(request);
                });
    }

    private Mono<Invoice> findEntityById(String id) {
        return invoiceRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Invoice with id " + id + " does not exist")));
    }

    private Flux<Invoice> findActiveInvoices() {
        Criteria criteria = Criteria.where("totalAmount").ne(null);
        Query query = new Query(criteria);

        return reactiveMongoTemplate.find(query, Invoice.class);
    }

    private Mono<Long> countActiveInvoices() {
        Criteria criteria = Criteria.where(TOTAL_AMOUNT).ne(null);
        Query query = new Query(criteria);

        return reactiveMongoTemplate.count(query, Long.class);
    }

    private void validateDateOfReturnOfTheCar(LocalDate dateOfReturnOfTheCar) {
        LocalDate currentDate = LocalDate.now();

        if (dateOfReturnOfTheCar.isBefore(currentDate)) {
            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date of return of the car cannot be in the past"
            );
        }
    }

    private Mono<Invoice> setupExistingInvoice(AuthenticationInfo authenticationInfo, String id, InvoiceRequest invoiceRequest) {
        return Mono.zip(
                findEntityById(id),
                bookingService.findBookingById(authenticationInfo, invoiceRequest.bookingId()),
                (existingInvoice, bookingResponse) -> getUpdatedInvoice(existingInvoice, invoiceRequest, bookingResponse)
        );
    }

    private Invoice getUpdatedInvoice(Invoice existingInvoice,
                                      InvoiceRequest invoiceRequest,
                                      BookingResponse bookingResponse) {
        Invoice updatedInvoice = invoiceMapper.getNewInvoiceInstance(existingInvoice);

        updatedInvoice.setCustomerUsername(bookingResponse.customerUsername());
        updatedInvoice.setCarReturnDate(invoiceRequest.carReturnDate());
        updatedInvoice.setReceptionistEmployeeId(MongoUtil.getObjectId(invoiceRequest.receptionistEmployeeId()));
        updatedInvoice.setReturnBranchId(MongoUtil.getObjectId(invoiceRequest.returnBranchId()));
        updatedInvoice.setCarId(MongoUtil.getObjectId(invoiceRequest.carId()));
        updatedInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        updatedInvoice.setDamageCost(getDamageCost(invoiceRequest));
        updatedInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        updatedInvoice.setComments(invoiceRequest.comments());
        updatedInvoice.setTotalAmount(getTotalAmount(invoiceRequest, bookingResponse));
        updatedInvoice.setInvoiceProcessStatus(InvoiceProcessStatus.IN_CLOSING);

        return updatedInvoice;
    }

    private Mono<Invoice> processInvoiceClosing(AuthenticationInfo authenticationInfo, Invoice invoice) {
        return updateProcesses(authenticationInfo, invoice)
                .filter(Boolean.TRUE::equals)
                .map(_ -> invoiceMapper.getSuccessfulCreatedInvoice(invoice))
                .flatMap(revenueService::processClosing)
                .switchIfEmpty(Mono.defer(() -> invoiceRepository.save(invoiceMapper.getFailedCreatedInvoice(invoice))));
    }

    private Mono<Boolean> updateProcesses(AuthenticationInfo authenticationInfo, Invoice invoice) {
        return Mono.zip(
                carService.setCarAsAvailable(authenticationInfo, getCarUpdateDetails(invoice), 5),
                bookingService.closeBooking(authenticationInfo, getBookingClosingDetails(invoice), 5),
                this::getUpdateResponse
        );
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoice) {
        return CarUpdateDetails.builder()
                .carId(invoice.getCarId().toString())
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId().toString())
                .carState(invoice.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .build();
    }

    private boolean getUpdateResponse(StatusUpdateResponse statusUpdateResponse, BookingUpdateResponse bookingUpdateResponse) {
        return statusUpdateResponse.isUpdateSuccessful() && bookingUpdateResponse.isSuccessful();
    }

    private BigDecimal getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? BigDecimal.ZERO : invoiceRequest.damageCost();
    }

    private BigDecimal getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ?
                BigDecimal.ZERO : invoiceRequest.additionalPayment();
    }

    private BookingClosingDetails getBookingClosingDetails(Invoice invoice) {
        return BookingClosingDetails.builder()
                .bookingId(invoice.getBookingId().toString())
                .returnBranchId(invoice.getReturnBranchId().toString())
                .build();
    }

    private BigDecimal getTotalAmount(InvoiceRequest invoiceRequest, BookingResponse bookingResponse) {
        LocalDate carReturnDate = invoiceRequest.carReturnDate();
        LocalDate bookingDateTo = bookingResponse.dateTo();
        LocalDate bookingDateFrom = bookingResponse.dateFrom();
        BigDecimal rentalCarPrice = bookingResponse.rentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getMoneyForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, rentalCarPrice);
        }

        return rentalCarPrice.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(getDamageCost(invoiceRequest));
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private BigDecimal getMoneyForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate bookingDateFrom,
                                             BigDecimal carAmount) {
        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateTo, carReturnDate) * 2L)));
    }

}
