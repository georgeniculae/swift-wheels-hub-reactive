package com.carrental.audit.util;

import com.carrental.document.dto.AuditLogInfoDto;
import com.carrental.document.model.AuditLogInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertAuditLogInfo(AuditLogInfo auditLogInfo, AuditLogInfoDto auditLogInfoDto) {
        assertEquals(auditLogInfo.getMethodName(), auditLogInfoDto.methodName());
        assertEquals(auditLogInfo.getUsername(), auditLogInfoDto.username());
        assertEquals(auditLogInfo.getParametersValues(), auditLogInfoDto.parametersValues());
    }

}
