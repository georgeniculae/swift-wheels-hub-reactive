package com.swiftwheelshub.booking.repository;

import com.swiftwheelshub.booking.model.Outbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OutboxRepository extends ReactiveMongoRepository<Outbox, ObjectId> {
}
