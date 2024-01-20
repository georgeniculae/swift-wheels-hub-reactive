package com.swiftwheelshub.customer.repository;

import com.swiftwheelshub.customer.model.Outbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OutboxRepository extends ReactiveMongoRepository<Outbox, ObjectId> {
}
