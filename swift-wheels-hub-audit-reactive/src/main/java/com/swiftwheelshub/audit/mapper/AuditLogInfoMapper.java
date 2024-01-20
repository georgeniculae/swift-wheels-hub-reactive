package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.dto.AuditLogInfoDto;
import com.swiftwheelshub.model.AuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface AuditLogInfoMapper {

    AuditLogInfoDto mapEntityToDto(AuditLogInfo auditLogInfo);

    AuditLogInfo mapDtoToEntity(AuditLogInfoDto auditLogInfoDto);

}
