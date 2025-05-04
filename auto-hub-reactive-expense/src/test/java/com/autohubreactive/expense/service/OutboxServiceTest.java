package com.autohubreactive.expense.service;

import com.autohubreactive.dto.BookingClosingDetails;
import com.autohubreactive.dto.CarUpdateDetails;
import com.autohubreactive.dto.InvoiceReprocessRequest;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.expense.mapper.InvoiceMapper;
import com.autohubreactive.expense.mapper.InvoiceMapperImpl;
import com.autohubreactive.expense.model.Outbox;
import com.autohubreactive.expense.producer.BookingUpdateProducerService;
import com.autohubreactive.expense.producer.CarStatusUpdateProducerService;
import com.autohubreactive.expense.producer.FailedInvoiceDlqProducerService;
import com.autohubreactive.expense.producer.InvoiceProducerService;
import com.autohubreactive.expense.repository.OutboxRepository;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.model.invoice.Invoice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxServiceTest {

    @InjectMocks
    private OutboxService outboxService;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private InvoiceProducerService invoiceProducerService;

    @Mock
    private BookingUpdateProducerService bookingUpdateProducerService;

    @Mock
    private CarStatusUpdateProducerService carStatusUpdateProducerService;

    @Mock
    private FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void handleOutboxesTest_success() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.empty());
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.empty());
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.empty());
        when(outboxRepository.delete(outbox)).thenReturn(Mono.empty());

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(failedInvoiceDlqProducerService, never()).reprocessInvoice(any(InvoiceReprocessRequest.class));
    }

    @Test
    void handleOutboxesTest_errorOnBookingUpdateFailed() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.empty());
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.empty());
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(failedInvoiceDlqProducerService.reprocessInvoice(any(InvoiceReprocessRequest.class)))
                .thenReturn(Mono.empty());
        when(outboxRepository.delete(outbox)).thenReturn(Mono.empty());

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(invoiceMapper).mapToInvoiceReprocessRequest(any(Invoice.class));
    }

    @Test
    void handleOutboxesTest_errorOnFindingOutboxes() {
        when(outboxRepository.findAll()).thenReturn(Flux.error(new RuntimeException("Test")));

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveOutboxTest() {
        Invoice savedInvoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.save(any(Outbox.class))).thenReturn(Mono.just(outbox));

        outboxService.saveOutbox(savedInvoice)
                .as(StepVerifier::create)
                .expectNext(outbox)
                .verifyComplete();

        verify(outboxRepository).save(any(Outbox.class));
    }

}
