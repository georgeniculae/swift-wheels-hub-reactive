package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingRollbackResponse;
import com.swiftwheelshubreactive.expense.model.FailedBookingRollback;
import com.swiftwheelshubreactive.expense.repository.FailedBookingRollbackRepository;
import com.swiftwheelshubreactive.expense.service.BookingService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedBookingRollbackSchedulerTest {

    @InjectMocks
    private FailedBookingRollbackScheduler failedBookingRollbackScheduler;

    @Mock
    private FailedBookingRollbackRepository failedBookingRollbackRepository;

    @Mock
    private BookingService bookingService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedBookingRollbackScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedBookingRollbackScheduler, "machineRole", "invoice_service");
    }

    @Test
    void processFailedBookingRollbackTest_success() {
        BookingRollbackResponse bookingRollbackResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingRollbackResponse.json", BookingRollbackResponse.class);

        FailedBookingRollback failedBookingRollback =
                TestUtil.getResourceAsJson("/data/FailedBookingRollback.json", FailedBookingRollback.class);

        when(failedBookingRollbackRepository.findAll()).thenReturn(Flux.just(failedBookingRollback));
        when(bookingService.rollbackBooking(any(AuthenticationInfo.class), anyString(), anyInt()))
                .thenReturn(Mono.just(bookingRollbackResponse));
        when(failedBookingRollbackRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        assertDoesNotThrow(() -> failedBookingRollbackScheduler.processFailedBookingRollback());
    }

}
