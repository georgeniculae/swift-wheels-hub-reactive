package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshubreactive.expense.model.Outbox;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.FailedInvoiceDlqProducerService;
import com.swiftwheelshubreactive.expense.producer.InvoiceProducerService;
import com.swiftwheelshubreactive.expense.repository.OutboxRepository;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Invoice;
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
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.just(true));
        when(outboxRepository.delete(outbox)).thenReturn(Mono.empty());

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(failedInvoiceDlqProducerService, never()).reprocessInvoice(any(InvoiceReprocessRequest.class));
    }

    @Test
    void handleOutboxesTest_carUpdateFailed_error() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(false));
        when(failedInvoiceDlqProducerService.reprocessInvoice(any(InvoiceReprocessRequest.class)))
                .thenReturn(Mono.empty());

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(invoiceMapper).mapToInvoiceReprocessRequest(any(Invoice.class));
        verify(bookingUpdateProducerService, never()).sendBookingClosingDetails(any(BookingClosingDetails.class));
    }

    @Test
    void handleOutboxesTest_bookingUpdateFailed_error() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.just(false));
        when(failedInvoiceDlqProducerService.reprocessInvoice(any(InvoiceReprocessRequest.class)))
                .thenReturn(Mono.empty());

        outboxService.handleOutboxes()
                .as(StepVerifier::create)
                .expectComplete()
                .verify();

        verify(invoiceMapper).mapToInvoiceReprocessRequest(any(Invoice.class));
        verify(outboxRepository, never()).delete(any(Outbox.class));
    }

    @Test
    void handleOutboxesTest_errorOnBookingUpdateFailed() {
        Outbox outbox = TestUtil.getResourceAsJson("/data/Outbox.json", Outbox.class);

        when(outboxRepository.findAll()).thenReturn(Flux.just(outbox));
        when(invoiceProducerService.sendInvoice(any(InvoiceResponse.class))).thenReturn(Mono.just(true));
        when(carStatusUpdateProducerService.sendCarUpdateDetails(any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(true));
        when(bookingUpdateProducerService.sendBookingClosingDetails(any(BookingClosingDetails.class)))
                .thenReturn(Mono.error(new RuntimeException("Test")));
        when(failedInvoiceDlqProducerService.reprocessInvoice(any(InvoiceReprocessRequest.class)))
                .thenReturn(Mono.empty());

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
