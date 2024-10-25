package com.swiftwheelshubreactive.expense.repository;

import com.swiftwheelshubreactive.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvoiceRepository extends ReactiveMongoRepository<Invoice, ObjectId> {

    Mono<Invoice> findByBookingId(ObjectId bookingId);

    @Query("""
            { 'comments': { $regex: '(?i)?0' } }""")
    Flux<Invoice> findByComments(String comments);

    @Query("""
            { 'invoiceProcessStatus': 'FAILED_CLOSED_INVOICE' }""")
    Flux<Invoice> findAllFailedInvoices();

    Flux<Invoice> findByCustomerUsername(String customerUsername);

    Mono<Boolean> existsByBookingId(ObjectId id);

}
