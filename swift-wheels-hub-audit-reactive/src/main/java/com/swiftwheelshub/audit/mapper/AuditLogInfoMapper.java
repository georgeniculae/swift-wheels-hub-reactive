package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.model.AuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface AuditLogInfoMapper {

    AuditLogInfoRequest mapEntityToDto(AuditLogInfo auditLogInfo);

    AuditLogInfo mapDtoToEntity(AuditLogInfoRequest auditLogInfoRequest);

}
