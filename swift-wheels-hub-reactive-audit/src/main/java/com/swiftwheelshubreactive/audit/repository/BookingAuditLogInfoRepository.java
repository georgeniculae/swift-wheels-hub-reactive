package com.swiftwheelshubreactive.audit.repository;

import com.swiftwheelshubreactive.model.BookingAuditLogInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BookingAuditLogInfoRepository extends ReactiveMongoRepository<BookingAuditLogInfo, ObjectId> {
}
