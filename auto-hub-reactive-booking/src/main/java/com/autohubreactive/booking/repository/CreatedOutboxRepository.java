package com.autohubreactive.booking.repository;

import com.autohubreactive.booking.model.CreatedOutbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CreatedOutboxRepository extends ReactiveMongoRepository<CreatedOutbox, ObjectId> {
}
