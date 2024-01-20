package com.carrental.booking.repository;

import com.carrental.document.model.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, ObjectId> {

    @Query("{ 'customerUsername': ?0 }")
    Flux<Booking> findByCustomerUsername(String customerUsername);

    @Query(value = "{ 'customerUsername': ?0 }", count = true)
    Mono<Long> countByCustomerUsername(String customerUsername);

    Mono<Void> deleteByCustomerUsername(String customerUsername);

}
