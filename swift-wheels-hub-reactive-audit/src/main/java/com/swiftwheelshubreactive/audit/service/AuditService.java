package com.swiftwheelshubreactive.audit.service;

import com.swiftwheelshubreactive.audit.mapper.BookingAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.CustomerAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.ExpenseAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.repository.BookingAuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.repository.CustomerAuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.repository.ExpenseAuditLogInfoRepository;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
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
        return bookingAuditLogInfoRepository.save(bookingAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .map(bookingAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving booking audit log: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<AuditLogInfoRequest> saveCustomerAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        return customerAuditLogInfoRepository.save(customerAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .map(customerAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving customer audit log: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<AuditLogInfoRequest> saveExpenseAuditLogInfo(AuditLogInfoRequest auditLogInfoRequest) {
        return expenseAuditLogInfoRepository.save(expenseAuditLogInfoMapper.mapDtoToEntity(auditLogInfoRequest))
                .map(expenseAuditLogInfoMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving expense audit log: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

}
