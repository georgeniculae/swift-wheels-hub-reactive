package com.swiftwheelshubreactive.booking.repository;

import com.swiftwheelshubreactive.booking.model.DeletedOutbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeletedOutboxRepository extends ReactiveMongoRepository<DeletedOutbox, ObjectId> {
}
