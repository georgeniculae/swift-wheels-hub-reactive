package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.mapper.AuditLogInfoMapperImpl;
import com.swiftwheelshub.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshub.audit.util.TestUtils;
import com.carrental.document.dto.AuditLogInfoDto;
import com.carrental.document.model.AuditLogInfo;
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
        AuditLogInfoDto auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoDto.json", AuditLogInfoDto.class);

        when(auditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(Mono.just(auditLogInfo));

        StepVerifier.create(auditService.saveAuditLogInfo(auditLogInfoDto))
                .expectNext(auditLogInfoDto)
                .verifyComplete();

        verify(auditLogInfoMapper, times(1)).mapEntityToDto(any(AuditLogInfo.class));
    }

    @Test
    void saveAuditLogInfoTest_errorOnSave() {
        AuditLogInfoDto auditLogInfoDto =
                TestUtils.getResourceAsJson("/data/CustomerAuditLogInfoDto.json", AuditLogInfoDto.class);

        when(auditLogInfoRepository.save(any(AuditLogInfo.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(auditService.saveAuditLogInfo(auditLogInfoDto))
                .expectError()
                .verify();
    }

}
