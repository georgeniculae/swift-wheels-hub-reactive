package com.swiftwheelshubreactive.audit.util;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.BookingAuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertAuditLogInfo(BookingAuditLogInfo bookingAuditLogInfo, AuditLogInfoRequest auditLogInfoRequest) {
        assertEquals(bookingAuditLogInfo.getMethodName(), auditLogInfoRequest.methodName());
        assertEquals(bookingAuditLogInfo.getUsername(), auditLogInfoRequest.username());
        assertEquals(bookingAuditLogInfo.getParametersValues(), auditLogInfoRequest.parametersValues());
    }

}
