package com.swiftwheelshub.audit.service;

import com.swiftwheelshub.audit.mapper.AuditLogInfoMapper;
import com.swiftwheelshub.audit.repository.AuditLogInfoRepository;
import com.swiftwheelshub.dto.AuditLogInfoDto;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
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
    public Mono<AuditLogInfoDto> saveAuditLogInfo(AuditLogInfoDto auditLogInfoDto) {
        return auditLogInfoRepository.save(auditLogInfoMapper.mapDtoToEntity(auditLogInfoDto))
                .map(auditLogInfoMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving audit log: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e));
                });
    }

}
