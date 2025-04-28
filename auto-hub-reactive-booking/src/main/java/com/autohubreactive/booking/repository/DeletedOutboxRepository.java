package com.autohubreactive.booking.repository;

import com.autohubreactive.booking.model.DeletedOutbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface DeletedOutboxRepository extends ReactiveMongoRepository<DeletedOutbox, ObjectId> {
}
