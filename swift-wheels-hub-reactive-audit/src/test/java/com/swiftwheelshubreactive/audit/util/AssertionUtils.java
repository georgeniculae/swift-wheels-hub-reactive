package com.swiftwheelshubreactive.audit.util;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.AuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertAuditLogInfo(AuditLogInfo auditLogInfo, AuditLogInfoRequest auditLogInfoRequest) {
        assertEquals(auditLogInfo.getMethodName(), auditLogInfoRequest.methodName());
        assertEquals(auditLogInfo.getUsername(), auditLogInfoRequest.username());
        assertEquals(auditLogInfo.getParametersValues(), auditLogInfoRequest.parametersValues());
    }

}
