package com.autohubreactive.audit.mapper;

import com.autohubreactive.dto.AuditLogInfoRequest;
import com.autohubreactive.model.CustomerAuditLogInfo;
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
