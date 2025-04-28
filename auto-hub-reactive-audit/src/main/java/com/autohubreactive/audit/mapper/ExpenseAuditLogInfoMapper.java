package com.autohubreactive.audit.mapper;

import com.autohubreactive.dto.AuditLogInfoRequest;
import com.autohubreactive.model.ExpenseAuditLogInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ExpenseAuditLogInfoMapper {

    AuditLogInfoRequest mapEntityToDto(ExpenseAuditLogInfo bookingAuditLogInfo);

    ExpenseAuditLogInfo mapDtoToEntity(AuditLogInfoRequest auditLogInfoRequest);

}
