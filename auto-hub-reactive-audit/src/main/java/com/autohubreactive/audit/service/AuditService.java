package com.autohubreactive.audit.service;

import com.autohubreactive.audit.mapper.BookingAuditLogInfoMapper;
import com.autohubreactive.audit.mapper.CustomerAuditLogInfoMapper;
import com.autohubreactive.audit.mapper.ExpenseAuditLogInfoMapper;
import com.autohubreactive.audit.repository.BookingAuditLogInfoRepository;
import com.autohubreactive.audit.repository.CustomerAuditLogInfoRepository;
import com.autohubreactive.audit.repository.ExpenseAuditLogInfoRepository;
import com.autohubreactive.dto.common.AuditLogInfoRequest;
import com.autohubreactive.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final BookingAuditLogInfoRepository bookingAuditLogInfoRepository;
    private final CustomerAuditLogInfoRepository customerAuditLogInfoRepository;
    private final ExpenseAuditLogInfoRepository expenseAuditLogInfoRepository;
    private final BookingAuditLogInfoMapper bookingAuditLogInfoMapper;
    private final CustomerAuditLogInfoMapper customerAuditLogInfoMapper;
    private final ExpenseAuditLogInfoMapper expenseAuditLogInfoMapper;

    public Mono<AuditLogInfoRequest> saveBookingAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        return Mono.just(bookingAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .flatMap(bookingAuditLogInfoRepository::save)
                .map(bookingAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving booking audit log: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<AuditLogInfoRequest> saveCustomerAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        return Mono.just(customerAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .flatMap(customerAuditLogInfoRepository::save)
                .map(customerAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving customer audit log: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<AuditLogInfoRequest> saveExpenseAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        return Mono.just(expenseAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .flatMap(expenseAuditLogInfoRepository::save)
                .map(expenseAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving expense audit log: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

}
