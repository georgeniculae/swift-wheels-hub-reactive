package com.autohubreactive.expense.consumer;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.expense.service.InvoiceService;
import com.autohubreactive.lib.retry.RetryHandler;
import com.autohubreactive.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CreatedBookingMessageConsumer {

    private final InvoiceService invoiceService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<BookingResponse>>, Mono<Void>> savedBookingConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<InvoiceResponse> processMessage(Message<BookingResponse> message) {
        return invoiceService.saveInvoice(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnSuccess(invoiceResponse -> {
                    if (ObjectUtils.isNotEmpty(invoiceResponse)) {
                        KafkaUtil.acknowledgeMessage(message.getHeaders());
                        log.info("Invoice saved: {}", invoiceResponse);

                        return;
                    }

                    log.info("Invoice: {} already exists", message.getPayload().id());
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
