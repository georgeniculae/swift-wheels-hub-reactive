package com.swiftwheelshubreactive.booking.repository;

import com.swiftwheelshubreactive.model.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, ObjectId> {

    @Query("""
            { 'status': 'FAILED' }""")
    Flux<Booking> findAllFailedBookings();

    @Query("""
            { 'customerUsername': ?0 }""")
    Flux<Booking> findByCustomerUsername(String customerUsername);

    @Query(
            value = """
                    { 'customerUsername': ?0 }""",
            count = true
    )
    Mono<Long> countByCustomerUsername(String customerUsername);

}
