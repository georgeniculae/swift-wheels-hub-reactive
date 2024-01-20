package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.expense.model.Outbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OutboxRepository extends ReactiveMongoRepository<Outbox, ObjectId> {
}
