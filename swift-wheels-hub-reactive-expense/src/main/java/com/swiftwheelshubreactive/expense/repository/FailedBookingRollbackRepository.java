package com.swiftwheelshubreactive.expense.repository;

import com.swiftwheelshubreactive.expense.model.FailedBookingRollback;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FailedBookingRollbackRepository extends ReactiveMongoRepository<FailedBookingRollback, ObjectId> {
}
