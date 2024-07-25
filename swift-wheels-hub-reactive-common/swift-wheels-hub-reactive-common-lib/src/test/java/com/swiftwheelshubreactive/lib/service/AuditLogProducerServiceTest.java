package com.swiftwheelshubreactive.lib.service;

import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.lib.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogProducerServiceTest {

    @InjectMocks
    private AuditLogProducerService auditLogProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Test
    void sendAuditLogTest_success() {
        ReflectionTestUtils.setField(auditLogProducerService, "auditLogBinderName", "audit-log-info-out-0");
        ReflectionTestUtils.setField(auditLogProducerService, "auditLogMimeType", "application/json");

        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(streamBridge.send(anyString(), any(Message.class), any(MimeType.class))).thenReturn(true);

        auditLogProducerService.sendAuditLog(auditLogInfoRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
