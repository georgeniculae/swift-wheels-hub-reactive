package com.swiftwheelshubreactive.audit.repository;

import com.swiftwheelshubreactive.model.AuditLogInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuditLogInfoRepository extends ReactiveMongoRepository<AuditLogInfo, ObjectId> {
}
