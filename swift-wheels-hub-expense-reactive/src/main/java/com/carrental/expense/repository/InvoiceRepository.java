package com.carrental.expense.repository;

import com.swiftwheelshub.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InvoiceRepository extends ReactiveMongoRepository<Invoice, ObjectId> {

    Mono<Invoice> findByBookingId(ObjectId bookingId);

    Flux<Invoice> findByComments(String comments);

    Flux<Invoice> findByCustomerUsername(String customerUsername);

    Mono<Boolean> existsByBookingId(ObjectId id);

}
