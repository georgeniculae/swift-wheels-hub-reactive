package com.carrental.audit.service;

import com.carrental.audit.mapper.AuditLogInfoMapper;
import com.carrental.audit.repository.AuditLogInfoRepository;
import com.carrental.document.dto.AuditLogInfoDto;
import com.carrental.lib.exceptionhandling.CarRentalException;
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

                    return Mono.error(new CarRentalException(e));
                });
    }

}
