package com.swiftwheelshubreactive.audit.service;

import com.swiftwheelshubreactive.audit.mapper.BookingAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.BookingAuditLogInfoMapperImpl;
import com.swiftwheelshubreactive.audit.mapper.CustomerAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.CustomerAuditLogInfoMapperImpl;
import com.swiftwheelshubreactive.audit.mapper.ExpenseAuditLogInfoMapper;
import com.swiftwheelshubreactive.audit.mapper.ExpenseAuditLogInfoMapperImpl;
import com.swiftwheelshubreactive.audit.repository.BookingAuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.repository.CustomerAuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.repository.ExpenseAuditLogInfoRepository;
import com.swiftwheelshubreactive.audit.util.TestUtil;
import com.swiftwheelshubreactive.dto.AuditLogInfoRequest;
import com.swiftwheelshubreactive.model.BookingAuditLogInfo;
import com.swiftwheelshubreactive.model.CustomerAuditLogInfo;
import com.swiftwheelshubreactive.model.ExpenseAuditLogInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditService auditService;

    @Mock
    private BookingAuditLogInfoRepository bookingAuditLogInfoRepository;

    @Mock
    private CustomerAuditLogInfoRepository customerAuditLogInfoRepository;

    @Mock
    private ExpenseAuditLogInfoRepository expenseAuditLogInfoRepository;

    @Spy
    private BookingAuditLogInfoMapper bookingAuditLogInfoMapper = new BookingAuditLogInfoMapperImpl();

    @Spy
    private CustomerAuditLogInfoMapper customerAuditLogInfoMapper = new CustomerAuditLogInfoMapperImpl();

    @Spy
    private ExpenseAuditLogInfoMapper expenseAuditLogInfoMapper = new ExpenseAuditLogInfoMapperImpl();

    @Test
    void saveBookingAuditLogInfoTest_success() {
        BookingAuditLogInfo bookingAuditLogInfo =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfo.json", BookingAuditLogInfo.class);
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/BookingAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(bookingAuditLogInfoRepository.save(any(BookingAuditLogInfo.class))).thenReturn(Mono.just(bookingAuditLogInfo));

        StepVerifier.create(auditService.saveBookingAuditLogInfo(auditLogInfoRequest))
                .expectNext(auditLogInfoRequest)
                .verifyComplete();

        verify(bookingAuditLogInfoMapper).mapEntityToDto(any(BookingAuditLogInfo.class));
    }

    @Test
    void saveBookingAuditLogInfoTest_errorOnSave() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(bookingAuditLogInfoRepository.save(any(BookingAuditLogInfo.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(auditService.saveBookingAuditLogInfo(auditLogInfoRequest))
                .expectError()
                .verify();
    }

    @Test
    void saveCustomerAuditLogInfoTest_success() {
        CustomerAuditLogInfo customerAuditLogInfo =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfo.json", CustomerAuditLogInfo.class);
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(customerAuditLogInfoRepository.save(any(CustomerAuditLogInfo.class))).thenReturn(Mono.just(customerAuditLogInfo));

        StepVerifier.create(auditService.saveCustomerAuditLogInfo(auditLogInfoRequest))
                .expectNext(auditLogInfoRequest)
                .verifyComplete();

        verify(customerAuditLogInfoMapper).mapEntityToDto(any(CustomerAuditLogInfo.class));
    }

    @Test
    void saveCustomerAuditLogInfoTest_errorOnSave() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/CustomerAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(customerAuditLogInfoRepository.save(any(CustomerAuditLogInfo.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(auditService.saveCustomerAuditLogInfo(auditLogInfoRequest))
                .expectError()
                .verify();
    }

    @Test
    void saveExpenseAuditLogInfoTest_success() {
        ExpenseAuditLogInfo expenseAuditLogInfo =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfo.json", ExpenseAuditLogInfo.class);
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(expenseAuditLogInfoRepository.save(any(ExpenseAuditLogInfo.class))).thenReturn(Mono.just(expenseAuditLogInfo));

        StepVerifier.create(auditService.saveExpenseAuditLogInfo(auditLogInfoRequest))
                .expectNext(auditLogInfoRequest)
                .verifyComplete();

        verify(expenseAuditLogInfoMapper).mapEntityToDto(any(ExpenseAuditLogInfo.class));
    }

    @Test
    void saveExpenseAuditLogInfoTest_errorOnSave() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/ExpenseAuditLogInfoRequest.json", AuditLogInfoRequest.class);

        when(expenseAuditLogInfoRepository.save(any(ExpenseAuditLogInfo.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(auditService.saveExpenseAuditLogInfo(auditLogInfoRequest))
                .expectError()
                .verify();
    }

}
