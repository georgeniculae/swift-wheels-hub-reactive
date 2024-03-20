package com.swiftwheelshubreactive.audit.mapper;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.BookingAuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookingAuditLogInfoMapper {

    AuditLogInfoRequest mapEntityToDto(BookingAuditLogInfo bookingAuditLogInfo);

    BookingAuditLogInfo mapDtoToEntity(AuditLogInfoRequest auditLogInfoRequest);

}
