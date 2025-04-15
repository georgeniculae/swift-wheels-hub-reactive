package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceReprocessingServiceTest {

    @InjectMocks
    private InvoiceReprocessingService invoiceReprocessingService;

    @Mock
    private BookingUpdateProducerService bookingUpdateProducerService;

    @Mock
    private CarStatusUpdateProducerService carStatusUpdateProducerService;

    @Test
    void reprocessInvoiceTest_success() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void reprocessInvoiceTest_updateBookingFailed() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.just(false));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void reprocessInvoiceTest_carUpdateFailed() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(false));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
