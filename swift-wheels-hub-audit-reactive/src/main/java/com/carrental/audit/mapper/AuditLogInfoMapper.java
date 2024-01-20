package com.carrental.audit.mapper;

import com.carrental.document.dto.AuditLogInfoDto;
import com.carrental.document.model.AuditLogInfo;
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
