package com.autohubreactive.expense.service;

import com.autohubreactive.dto.common.BookingClosingDetails;
import com.autohubreactive.dto.common.CarUpdateDetails;
import com.autohubreactive.dto.invoice.InvoiceReprocessRequest;
import com.autohubreactive.expense.producer.BookingUpdateProducerService;
import com.autohubreactive.expense.producer.CarStatusUpdateProducerService;
import com.autohubreactive.expense.util.TestUtil;
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
                .thenReturn(Mono.empty());
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.empty());

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void reprocessInvoiceTest_carUpdateFailed() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void reprocessInvoiceTest_updateBookingFailed() {
        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.empty());
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
