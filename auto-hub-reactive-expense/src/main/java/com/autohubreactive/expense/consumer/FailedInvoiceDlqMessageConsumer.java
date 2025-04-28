package com.autohubreactive.expense.consumer;

import com.autohubreactive.dto.InvoiceReprocessRequest;
import com.autohubreactive.expense.service.InvoiceReprocessingService;
import com.autohubreactive.lib.retry.RetryHandler;
import com.autohubreactive.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FailedInvoiceDlqMessageConsumer {

    private final InvoiceReprocessingService invoiceReprocessingService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<InvoiceReprocessRequest>>, Mono<Void>> failedInvoiceDlqConsumer() {
        return messageFlux -> messageFlux.concatMap(this::reprocessInvoice)
                .then();
    }

    public Mono<Void> reprocessInvoice(Message<InvoiceReprocessRequest> message) {
        return invoiceReprocessingService.reprocessInvoice(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Invoice: {} reprocessed successfully", message.getPayload().invoiceId());
                })
                .onErrorResume(e -> {
                    log.info("Error while reprocessing invoice: {}", e.getMessage());

                    return Mono.empty();
                });
    }

}
