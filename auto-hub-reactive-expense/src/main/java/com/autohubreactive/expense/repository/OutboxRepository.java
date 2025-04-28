package com.autohubreactive.expense.repository;

import com.autohubreactive.expense.model.Outbox;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface OutboxRepository extends ReactiveMongoRepository<Outbox, ObjectId> {
}
