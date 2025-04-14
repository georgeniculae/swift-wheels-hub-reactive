package com.swiftwheelshubreactive.booking.producer.dlq;

import com.swiftwheelshubreactive.booking.service.dlq.CreatedBookingReprocessService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatedBookingReprocessServiceTest {

    @InjectMocks
    private CreatedBookingReprocessService createdBookingReprocessService;

}
