package com.swiftwheelshubreactive.audit.repository;

import com.swiftwheelshubreactive.model.CustomerAuditLogInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CustomerAuditLogInfoRepository extends ReactiveMongoRepository<CustomerAuditLogInfo, ObjectId> {
}
