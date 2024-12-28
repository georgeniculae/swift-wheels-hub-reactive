package com.swiftwheelshubreactive.expense.producer;

import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;

@ExtendWith(MockitoExtension.class)
class BookingRollbackProducerServiceTest {

    @InjectMocks
    private BookingRollbackProducerService bookingRollbackProducerService;

    @Mock
    private StreamBridge streamBridge;

    @Mock
    private RetryHandler retryHandler;



}
