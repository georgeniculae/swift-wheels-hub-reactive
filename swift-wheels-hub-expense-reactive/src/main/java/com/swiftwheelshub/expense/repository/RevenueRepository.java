package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.model.Revenue;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RevenueRepository extends ReactiveMongoRepository<Revenue, ObjectId> {

    @Aggregation(pipeline = {"{$group: { _id: '', totalAmount: {$sum: $amountFromBooking}}}"})
    Mono<Double> getTotalAmount();

}