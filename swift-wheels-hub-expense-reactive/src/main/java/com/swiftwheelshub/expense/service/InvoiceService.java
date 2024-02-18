package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.lib.aspect.LogActivity;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

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
    private final InvoiceMapper invoiceMapper;

    public Flux<InvoiceResponse> findAllInvoices() {
        return invoiceRepository.findAll()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceResponse> findAllActiveInvoices() {
        return findActiveInvoices()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all active invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceResponse> findAllInvoicesByCustomerUsername(String customerUsername) {
        return invoiceRepository.findByCustomerUsername(customerUsername)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoices by customer id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<InvoiceResponse> findInvoiceById(String id) {
        return findEntityById(id)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoice by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceResponse> findInvoicesByComments(String comments) {
        return invoiceRepository.findByComments(comments)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Invoice with comment: " + comments + " does not exist"
                                )
                        )
                )
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoices by comments: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countInvoices() {
        return invoiceRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting all invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countAllActiveInvoices() {
        return countActiveInvoices()
                .onErrorResume(e -> {
                    log.error("Error while counting all active invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
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
                .flatMap(existsByBookingId -> invoiceRepository.save(getInvoice(newBookingResponse)))
                .map(invoiceMapper::mapEntityToDto);
    }

    public Mono<InvoiceResponse> updateInvoiceAfterBookingUpdate(BookingResponse newBookingResponse) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(newBookingResponse.id()))
                .flatMap(invoice -> {
                    invoice.setCarId(MongoUtil.getObjectId(newBookingResponse.carId()));

                    return invoiceRepository.save(invoice);
                })
                .map(invoiceMapper::mapEntityToDto);
    }

    @LogActivity(
            sentParameters = {"id", "invoiceRequest"},
            activityDescription = "Invoice closing"
    )
    public Mono<InvoiceResponse> closeInvoice(String apiKeyToken, String id, InvoiceRequest invoiceRequest) {
        return validateInvoice(invoiceRequest)
                .flatMap(request -> findEntityById(id))
                .zipWith(bookingService.findBookingById(apiKeyToken, invoiceRequest.bookingId()))
                .map(existingInvoiceAndBookingRequest -> updateInvoiceWithBookingDetails(invoiceRequest, existingInvoiceAndBookingRequest))
                .flatMap(revenueService::saveInvoiceRevenueAndOutboxTransactional)
                .delayUntil(invoice -> bookingService.closeBooking(apiKeyToken, getBookingClosingDetails(invoice)))
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while closing invoice: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @Transactional
    public Mono<Void> deleteInvoiceByBookingId(String bookingId) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(bookingId))
                .filter(invoice -> ObjectUtils.isEmpty(invoice.getTotalAmount()))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invoice cannot be deleted if booking is in progress"
                                )
                        )
                )
                .flatMap(invoice -> invoiceRepository.deleteById(invoice.getId()));
    }

    private Invoice getInvoice(BookingResponse newBookingResponse) {
        Invoice invoice = new Invoice();

        invoice.setCustomerUsername(newBookingResponse.customerUsername());
        invoice.setCustomerEmail(newBookingResponse.customerEmail());
        invoice.setCarId(MongoUtil.getObjectId(newBookingResponse.carId()));
        invoice.setBookingId(MongoUtil.getObjectId(newBookingResponse.id()));

        return invoice;
    }

    private Mono<InvoiceRequest> validateInvoice(InvoiceRequest invoiceRequest) {
        return Mono.just(invoiceRequest)
                .flatMap(request -> {
                    LocalDate dateOfReturnOfTheCar = Optional.ofNullable((invoiceRequest.carDateOfReturn()))
                            .orElseThrow(() -> new SwiftWheelsHubException("Car return date is null"));

                    validateDateOfReturnOfTheCar(dateOfReturnOfTheCar);

                    if (Boolean.TRUE.equals(invoiceRequest.isVehicleDamaged()) &&
                            ObjectUtils.isEmpty(invoiceRequest.damageCost())) {
                        return Mono.error(new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "If the vehicle is damaged, the damage cost cannot be null/empty"
                                )
                        );
                    }

                    return Mono.just(request);
                });
    }

    private Mono<Invoice> findEntityById(String id) {
        return invoiceRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Invoice with id " + id + " does not exist"
                                )
                        )
                );
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

    private Invoice updateInvoiceWithBookingDetails(InvoiceRequest invoiceRequest,
                                                    Tuple2<Invoice, BookingResponse> existingInvoiceBookingResponse) {
        Invoice existingInvoice = existingInvoiceBookingResponse.getT1();
        BookingResponse bookingResponse = existingInvoiceBookingResponse.getT2();
        String receptionistEmployeeIdAsString = invoiceRequest.receptionistEmployeeId();
        String carId = invoiceRequest.carId();
        ObjectId receptionistEmployeeId = MongoUtil.getObjectId(receptionistEmployeeIdAsString);
        Invoice existingInvoiceUpdated = updateExistingInvoice(existingInvoice, invoiceRequest, carId, receptionistEmployeeId);

        return updateInvoiceAmount(bookingResponse, existingInvoiceUpdated, bookingResponse.rentalCarPrice());
    }

    private Invoice updateExistingInvoice(Invoice existingInvoice, InvoiceRequest invoiceRequest, String carId,
                                          ObjectId receptionistEmployeeId) {
        existingInvoice.setCarDateOfReturn(invoiceRequest.carDateOfReturn());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(MongoUtil.getObjectId(carId));
        existingInvoice.setIsVehicleDamaged(invoiceRequest.isVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceRequest));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceRequest));
        existingInvoice.setComments(invoiceRequest.comments());

        return existingInvoice;
    }

    private double getDamageCost(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.damageCost()) ? 0D : invoiceRequest.damageCost().doubleValue();
    }

    private double getAdditionalPayment(InvoiceRequest invoiceRequest) {
        return ObjectUtils.isEmpty(invoiceRequest.additionalPayment()) ?
                0D : invoiceRequest.additionalPayment().doubleValue();
    }

    private BookingClosingDetails getBookingClosingDetails(Invoice invoice) {
        Boolean isVehicleDamaged = Optional.ofNullable(invoice.getIsVehicleDamaged())
                .orElseThrow(() -> new SwiftWheelsHubException("isVehicleDamaged is null"));

        return BookingClosingDetails.builder()
                .bookingId(invoice.getBookingId().toString())
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId().toString())
                .carState(getCarStatus(isVehicleDamaged))
                .build();
    }

    private Double getTotalAmount(Invoice existingInvoice, BookingResponse bookingResponse, BigDecimal rentalCarPrice) {
        LocalDate carReturnDate = existingInvoice.getCarDateOfReturn();
        LocalDate bookingDateTo = bookingResponse.dateTo();
        LocalDate bookingDateFrom = bookingResponse.dateFrom();
        double carAmount = rentalCarPrice.doubleValue();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getMoneyForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                (ObjectUtils.isEmpty(existingInvoice.getDamageCost()) ? 0D : existingInvoice.getDamageCost());
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private double getMoneyForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate bookingDateFrom,
                                         Double carAmount) {
        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                getDaysPeriod(bookingDateTo, carReturnDate) * 2 * carAmount;
    }

    private CarState getCarStatus(boolean isVehicleDamaged) {
        return Boolean.TRUE.equals(isVehicleDamaged) ? CarState.BROKEN : CarState.AVAILABLE;
    }

    private Invoice updateInvoiceAmount(BookingResponse bookingResponse, Invoice existingInvoice, BigDecimal rentalCarPrice) {
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingResponse, rentalCarPrice));

        return existingInvoice;
    }

}
