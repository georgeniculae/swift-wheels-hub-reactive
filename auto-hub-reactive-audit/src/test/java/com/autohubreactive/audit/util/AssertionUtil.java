package com.autohubreactive.audit.util;

import com.autohubreactive.dto.common.AuditLogInfoRequest;
import com.autohubreactive.model.audit.BookingAuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertAuditLogInfo(BookingAuditLogInfo bookingAuditLogInfo, AuditLogInfoRequest auditLogInfoRequest) {
        assertEquals(bookingAuditLogInfo.getMethodName(), auditLogInfoRequest.methodName());
        assertEquals(bookingAuditLogInfo.getUsername(), auditLogInfoRequest.username());
        assertEquals(bookingAuditLogInfo.getParametersValues(), auditLogInfoRequest.parametersValues());
    }

}
