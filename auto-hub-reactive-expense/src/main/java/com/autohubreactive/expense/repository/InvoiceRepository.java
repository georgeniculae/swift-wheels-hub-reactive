package com.autohubreactive.expense.repository;

import com.autohubreactive.model.Invoice;
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

    Flux<Invoice> findByCustomerUsername(String customerUsername);

    Mono<Boolean> existsByBookingId(ObjectId id);

}
