package com.autohubreactive.booking.repository;

import com.autohubreactive.booking.model.UpdatedOutbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UpdateOutboxRepository extends ReactiveMongoRepository<UpdatedOutbox, ObjectId> {
}
