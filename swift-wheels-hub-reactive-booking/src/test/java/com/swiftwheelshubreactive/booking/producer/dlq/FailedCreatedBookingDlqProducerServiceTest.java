package com.swiftwheelshubreactive.booking.producer.dlq;

import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MimeType;
import reactor.test.StepVerifier;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedCreatedBookingDlqProducerServiceTest {

    @InjectMocks
    private FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private RetryHandler retryHandler;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(failedCreatedBookingDlqProducerService, "binderName", "failedCreatedBookingDlqBinderName");
        ReflectionTestUtils.setField(failedCreatedBookingDlqProducerService, "mimeType", "application/json");
    }

    @Test
    void sendCreatedBookingReprocessRequestTest_success() {
        CreatedBookingReprocessRequest createdBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        when(streamBridge.send(anyString(), any(Object.class), any(MimeType.class))).thenReturn(true);
        when(retryHandler.retry()).thenReturn(RetrySpec.backoff(0, Duration.ofMinutes(0)));

        failedCreatedBookingDlqProducerService.sendCreatedBookingReprocessRequest(createdBookingReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
