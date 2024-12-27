package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.lib.util.KafkaUtil;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
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
public class DeletedBookingMessageConsumer {

    private final InvoiceService invoiceService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<String>>, Mono<Void>> deletedBookingConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<Void> processMessage(Message<String> message) {
        return invoiceService.deleteInvoiceByBookingId(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(_ -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Invoice deleted for booking id: {}", message.getPayload());
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
