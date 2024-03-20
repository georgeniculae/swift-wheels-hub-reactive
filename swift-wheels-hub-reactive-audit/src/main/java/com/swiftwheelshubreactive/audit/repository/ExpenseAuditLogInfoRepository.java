package com.swiftwheelshubreactive.audit.repository;

import com.swiftwheelshubreactive.model.ExpenseAuditLogInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ExpenseAuditLogInfoRepository extends ReactiveMongoRepository<ExpenseAuditLogInfo, ObjectId> {
}
