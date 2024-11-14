package com.swiftwheelshubreactive.booking.repository;

import com.swiftwheelshubreactive.model.Booking;
import com.swiftwheelshubreactive.model.BookingStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BookingRepository extends ReactiveMongoRepository<Booking, ObjectId> {

    @Query("""
            { 'bookingProcessState': { $regex: '(?i)FAILED' } }""")
    Flux<Booking> findAllFailedBookings();

    @Query("""
            { 'customerUsername': ?0 }""")
    Flux<Booking> findByCustomerUsername(String customerUsername);

    Mono<Boolean> existsByCustomerUsernameAndStatus(String customerUsername, BookingStatus status);

    @Query(
            value = """
                    { 'customerUsername': ?0 }""",
            count = true
    )
    Mono<Long> countByCustomerUsername(String customerUsername);

    @Query(value = "{ '_id': ?0 }")
    @Update(value = "{'status': 'IN_PROGRESS'}")
    Mono<Void> updateBookingStatus(ObjectId id);

}
