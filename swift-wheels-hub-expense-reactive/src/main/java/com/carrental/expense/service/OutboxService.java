package com.carrental.expense.service;

import com.carrental.document.model.Invoice;
import com.carrental.expense.mapper.InvoiceMapper;
import com.carrental.expense.model.Outbox;
import com.carrental.expense.repository.OutboxRepository;
import com.carrental.lib.exceptionhandling.CarRentalException;
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
                .filter(outbox -> Outbox.Operation.CLOSE.equals(outbox.getOperation()))
                .delayUntil(outbox -> invoiceProducerService.sendInvoice(invoiceMapper.mapEntityToDto(outbox.getContent()))
                        .filter(Boolean.TRUE::equals)
                        .switchIfEmpty(Mono.error(new CarRentalException("Sending invoice failed"))))
                .flatMap(outboxRepository::delete);
    }

    public Mono<Outbox> saveOutbox(Invoice savedInvoice, Outbox.Operation operation) {
        Outbox outbox = Outbox.builder()
                .operation(operation)
                .content(savedInvoice)
                .build();

        return outboxRepository.save(outbox);
    }

}
