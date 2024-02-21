package com.swiftwheelshubreactive.audit.service;

import com.swiftwheelshubreactive.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.AuditLogInfoMapperImpl;
import com.swiftwheelshubreactive.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.util.TestUtils;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.AuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditService auditService;

    @Mock
    private AuditLogInfoRepository auditLogInfoRepository;

    @Spy
    private AuditLogInfoMapper auditLogInfoMapper = new AuditLogInfoMapperImpl();

    @Test
    void saveAuditLogInfoTest_success() {
        AuditLogInfo auditLogInfo =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfo.json", AuditLogInfo.class);
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(auditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(Mono.just(auditLogInfo));

        StepVerifier.create(auditService.saveAuditLogInfo(auditLogInfoRequest))
                .expectNext(auditLogInfoRequest)
                .verifyComplete();

        verify(auditLogInfoMapper, times(1)).mapEntityToDto(any(AuditLogInfo.class));
    }

    @Test
    void saveAuditLogInfoTest_errorOnSave() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(auditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(auditService.saveAuditLogInfo(auditLogInfoRequest))
                .expectError()
                .verify();
    }

}
