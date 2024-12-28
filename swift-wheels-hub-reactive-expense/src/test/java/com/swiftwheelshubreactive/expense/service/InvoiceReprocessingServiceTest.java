package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshubreactive.expense.producer.BookingRollbackProducerService;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Invoice;
import org.bson.types.ObjectId;
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
class InvoiceReprocessingServiceTest {

    @InjectMocks
    private InvoiceReprocessingService invoiceReprocessingService;

    @Mock
    private BookingUpdateProducerService bookingUpdateProducerService;

    @Mock
    private CarStatusUpdateProducerService carStatusUpdateProducerService;

    @Mock
    private BookingRollbackProducerService bookingRollbackProducerService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void reprocessInvoiceTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceReprocessRequest invoiceReprocessRequest =
                TestUtil.getResourceAsJson("/data/InvoiceReprocessRequest.json", InvoiceReprocessRequest.class);

        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));
        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.just(invoice));

        invoiceReprocessingService.reprocessInvoice(invoiceReprocessRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(invoiceMapper).getSuccessfulCreatedInvoice(any(Invoice.class));
    }

}
