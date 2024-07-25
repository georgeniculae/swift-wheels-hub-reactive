package com.swiftwheelshubreactive.audit.mapper;

import com.swiftwheelshubreactive.audit.util.AssertionUtils;
import com.swiftwheelshubreactive.audit.util.TestUtil;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.BookingAuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingBookingAuditLogInfoMapperTest {

    private final BookingAuditLogInfoMapper bookingAuditLogInfoMapper = new BookingAuditLogInfoMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        BookingAuditLogInfo bookingAuditLogInfo =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfo.json", BookingAuditLogInfo.class);

        AuditLogInfoRequest auditLogInfoDto = bookingAuditLogInfoMapper.mapEntityToDto(bookingAuditLogInfo);

        AssertionUtils.assertAuditLogInfo(bookingAuditLogInfo, auditLogInfoDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        BookingAuditLogInfo bookingAuditLogInfo = bookingAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest);

        AssertionUtils.assertAuditLogInfo(bookingAuditLogInfo, auditLogInfoRequest);
    }

}
