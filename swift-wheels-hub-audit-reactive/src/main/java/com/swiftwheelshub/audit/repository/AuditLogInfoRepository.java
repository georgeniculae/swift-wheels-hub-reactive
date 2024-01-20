package com.swiftwheelshub.audit.repository;

import com.carrental.document.model.AuditLogInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface AuditLogInfoRepository extends ReactiveMongoRepository<AuditLogInfo, ObjectId> {
}
