package com.autohubreactive.expense.service;

import com.autohubreactive.dto.common.BookingClosingDetails;
import com.autohubreactive.dto.agency.CarState;
import com.autohubreactive.dto.common.CarUpdateDetails;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.expense.mapper.InvoiceMapper;
import com.autohubreactive.expense.model.Outbox;
import com.autohubreactive.expense.producer.BookingUpdateProducerService;
import com.autohubreactive.expense.producer.CarStatusUpdateProducerService;
import com.autohubreactive.expense.producer.FailedInvoiceDlqProducerService;
import com.autohubreactive.expense.producer.InvoiceProducerService;
import com.autohubreactive.expense.repository.OutboxRepository;
import com.autohubreactive.model.invoice.Invoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final InvoiceProducerService invoiceProducerService;
    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final FailedInvoiceDlqProducerService failedInvoiceDlqProducerService;
    private final InvoiceMapper invoiceMapper;

    public Flux<Void> handleOutboxes() {
        return outboxRepository.findAll()
                .delayUntil(this::processInvoice)
                .concatMap(outboxRepository::delete)
                .onErrorMap(e -> {
                    log.error("Error while processing outbox: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
                });
    }

    public Mono<Outbox> saveOutbox(Invoice savedInvoice) {
        return outboxRepository.save(createOutbox(savedInvoice));
    }

    private Mono<Void> processInvoice(Outbox outbox) {
        return carStatusUpdateProducerService.sendCarUpdateDetails(getCarUpdateDetails(outbox.getContent()))
                .then(Mono.defer(() -> invoiceProducerService.sendInvoice(invoiceMapper.mapEntityToDto(outbox.getContent()))))
                .then(Mono.defer(() -> bookingUpdateProducerService.sendBookingClosingDetails(getBookingClosingDetails(outbox.getContent()))))
                .onErrorResume(e -> {
                    log.error("Error while processing invoice: {}", e.getMessage());

                    return reprocessInvoice(outbox);
                });
    }

    private Outbox createOutbox(Invoice savedInvoice) {
        return Outbox.builder()
                .content(savedInvoice)
                .build();
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoice) {
        return CarUpdateDetails.builder()
                .carId(invoice.getCarId().toString())
                .carState(invoice.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .receptionistEmployeeId(invoice.getReceptionistEmployeeId().toString())
                .build();
    }

    private BookingClosingDetails getBookingClosingDetails(Invoice invoice) {
        return BookingClosingDetails.builder()
                .bookingId(invoice.getBookingId().toString())
                .returnBranchId(invoice.getReturnBranchId().toString())
                .build();
    }

    private Mono<Void> reprocessInvoice(Outbox outbox) {
        return failedInvoiceDlqProducerService.reprocessInvoice(invoiceMapper.mapToInvoiceReprocessRequest(outbox.getContent()));
    }

}
