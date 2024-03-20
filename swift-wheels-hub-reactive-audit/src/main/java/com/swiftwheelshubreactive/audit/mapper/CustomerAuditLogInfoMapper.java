package com.swiftwheelshubreactive.audit.mapper;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.CustomerAuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CustomerAuditLogInfoMapper {

    AuditLogInfoRequest mapEntityToDto(CustomerAuditLogInfo customerAuditLogInfo);

    CustomerAuditLogInfo mapDtoToEntity(AuditLogInfoRequest auditLogInfoRequest);

}
