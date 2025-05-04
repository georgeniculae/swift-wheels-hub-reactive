package com.autohubreactive.expense.service;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.InvoiceRequest;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import com.autohubreactive.expense.mapper.InvoiceMapper;
import com.autohubreactive.expense.repository.InvoiceRepository;
import com.autohubreactive.lib.aspect.LogActivity;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.lib.util.MongoUtil;
import com.autohubreactive.model.invoice.Invoice;
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

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private static final String TOTAL_AMOUNT = "totalAmount";
    private final InvoiceRepository invoiceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RevenueService revenueService;
    private final InvoiceMapper invoiceMapper;

    public Flux<InvoiceResponse> findAllInvoices() {
        return invoiceRepository.findAll()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all invoices: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Flux<InvoiceResponse> findAllActiveInvoices() {
        return findActiveInvoices()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all active invoices: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
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
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Invoice with comment: " + comments + " does not exist")))
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

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<Long> countAllActiveInvoices() {
        return countActiveInvoices()
                .onErrorMap(e -> {
                    log.error("Error while counting all active invoices: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<InvoiceResponse> saveInvoice(BookingResponse newBookingResponse) {
        return invoiceRepository.existsByBookingId(MongoUtil.getObjectId(newBookingResponse.id()))
                .filter(Boolean.FALSE::equals)
                .flatMap(_ -> invoiceRepository.save(invoiceMapper.getNewInvoice(newBookingResponse)))
                .map(invoiceMapper::mapEntityToDto);
    }

    public Mono<InvoiceResponse> updateInvoiceAfterBookingUpdate(BookingResponse newBookingResponse) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(newBookingResponse.id()))
                .flatMap(invoice -> {
                    Invoice updatedInvoice = invoiceMapper.getInvoiceAfterBookingUpdate(invoice, newBookingResponse);

                    return invoiceRepository.save(updatedInvoice);
                })
                .map(invoiceMapper::mapEntityToDto);
    }

    @LogActivity(
            sentParameters = {"id", "invoiceRequest"},
            activityDescription = "Invoice closing"
    )
    public Mono<InvoiceResponse> closeInvoice(String id, InvoiceRequest invoiceRequest) {
        return validateInvoice(invoiceRequest)
                .flatMap(request -> updateExistingInvoice(id, request))
                .flatMap(revenueService::processClosing)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while closing invoice: {}, storing message to DLQ", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<Void> deleteInvoiceByBookingId(String bookingId) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(bookingId))
                .flatMap(invoice -> invoiceRepository.deleteById(invoice.getId()));
    }

    private Mono<InvoiceRequest> validateInvoice(InvoiceRequest invoiceRequest) {
        return Mono.just(invoiceRequest)
                .handle((request, sink) -> {
                    LocalDate carReturnDate = getCarReturnDate(request);

                    if (carReturnDate.isBefore(LocalDate.now())) {
                        sink.error(getReturnDateException());

                        return;
                    }

                    if (request.isVehicleDamaged() && ObjectUtils.isEmpty(request.damageCost())) {
                        sink.error(getDamageCostException());

                        return;
                    }

                    sink.next(request);
                });
    }

    private Mono<Invoice> findEntityById(String id) {
        return invoiceRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Invoice with id " + id + " does not exist")));
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

    private Mono<Invoice> updateExistingInvoice(String id, InvoiceRequest invoiceRequest) {
        return findEntityById(id)
                .map(existingInvoice -> invoiceMapper.getUpdatedInstance(existingInvoice, invoiceRequest));
    }

    private LocalDate getCarReturnDate(InvoiceRequest request) {
        return Optional.ofNullable((request.carReturnDate()))
                .orElseThrow(() -> new AutoHubException("Car return date is null"));
    }

    private AutoHubResponseStatusException getReturnDateException() {
        return new AutoHubResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Date of return of the car cannot be in the past"
        );
    }

    private static AutoHubResponseStatusException getDamageCostException() {
        return new AutoHubResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "If the vehicle is damaged, the damage cost cannot be null/empty"
        );
    }

}
