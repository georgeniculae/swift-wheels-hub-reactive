package com.swiftwheelshub.audit.mapper;

import com.swiftwheelshub.audit.util.AssertionUtils;
import com.swiftwheelshub.audit.util.TestUtils;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import com.swiftwheelshub.model.AuditLogInfo;
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

        AuditLogInfoRequest auditLogInfoDto = auditLogInfoMapper.mapEntityToDto(auditLogInfo);

        AssertionUtils.assertAuditLogInfo(auditLogInfo, auditLogInfoDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        AuditLogInfo auditLogInfo = auditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest);

        AssertionUtils.assertAuditLogInfo(auditLogInfo, auditLogInfoRequest);
    }

}
