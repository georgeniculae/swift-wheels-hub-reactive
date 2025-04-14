package com.swiftwheelshubreactive.booking.repository;

import com.swiftwheelshubreactive.booking.model.CreatedOutbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CreatedOutboxRepository extends ReactiveMongoRepository<CreatedOutbox, ObjectId> {
}
