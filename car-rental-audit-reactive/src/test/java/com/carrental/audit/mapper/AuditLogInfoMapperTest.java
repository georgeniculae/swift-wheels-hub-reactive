package com.carrental.audit.mapper;

import com.carrental.audit.util.AssertionUtils;
import com.carrental.audit.util.TestUtils;
import com.carrental.document.dto.AuditLogInfoDto;
import com.carrental.document.model.AuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditLogInfoMapperTest {

    private final AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        AuditLogInfo auditLogInfo =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfo.json", AuditLogInfo.class);

        AuditLogInfoDto auditLogInfoDto = auditLogInfoMapper.mapEntityToDto(auditLogInfo);

        AssertionUtils.assertAuditLogInfo(auditLogInfo, auditLogInfoDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        AuditLogInfoDto auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoDto.json", AuditLogInfoDto.class);

        AuditLogInfo auditLogInfo = auditLogInfoMapper.mapDtoToEntity(auditLogInfoDto);

        AssertionUtils.assertAuditLogInfo(auditLogInfo, auditLogInfoDto);
    }

}
