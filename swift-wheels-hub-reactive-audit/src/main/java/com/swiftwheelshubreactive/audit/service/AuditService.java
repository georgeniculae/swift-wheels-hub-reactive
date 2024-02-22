package com.swiftwheelshubreactive.audit.service;

import com.swiftwheelshubreactive.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditLogInfoRepository auditLogInfoRepository;
    private final AuditLogInfoMapper auditLogInfoMapper;

    @Transactional
    public Mono<AuditLogInfoRequest> saveAuditLogInfo(AuditLogInfoRequest auditLogInfoDto) {
        return auditLogInfoRepository.save(auditLogInfoMapper.mapDtoToEntity(auditLogInfoDto))
                .map(auditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving audit log: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

}
