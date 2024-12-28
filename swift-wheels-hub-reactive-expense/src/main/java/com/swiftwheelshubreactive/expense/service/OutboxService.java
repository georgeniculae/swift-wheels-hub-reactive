package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.model.Outbox;
import com.swiftwheelshubreactive.expense.producer.InvoiceProducerService;
import com.swiftwheelshubreactive.expense.repository.OutboxRepository;
import com.swiftwheelshubreactive.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final InvoiceProducerService invoiceProducerService;
    private final InvoiceMapper invoiceMapper;

    public Flux<Void> handleOutboxes() {
        return outboxRepository.findAll()
                .delayUntil(this::processInvoice)
                .flatMap(outboxRepository::delete);
    }

    public Mono<Outbox> saveOutbox(Invoice savedInvoice) {
        return outboxRepository.save(createOutbox(savedInvoice));
    }

    private Mono<Boolean> processInvoice(Outbox outbox) {
        return invoiceProducerService.sendInvoice(invoiceMapper.mapEntityToDto(outbox.getContent()))
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Sending invoice failed")));
    }

    private Outbox createOutbox(Invoice savedInvoice) {
        return Outbox.builder()
                .content(savedInvoice)
                .build();
    }

}
