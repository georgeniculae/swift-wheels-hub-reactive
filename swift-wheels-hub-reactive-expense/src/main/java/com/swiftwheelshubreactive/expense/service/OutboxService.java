package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.model.Outbox;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.FailedInvoiceDlqProducerService;
import com.swiftwheelshubreactive.expense.producer.InvoiceProducerService;
import com.swiftwheelshubreactive.expense.repository.OutboxRepository;
import com.swiftwheelshubreactive.model.Invoice;
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

                    return new SwiftWheelsHubException(e.getMessage());
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
