package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import com.swiftwheelshubreactive.lib.util.KafkaUtil;
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
public class UpdatedBookingMessageConsumer {

    private final InvoiceService invoiceService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<BookingResponse>>, Mono<Void>> updatedBookingConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<InvoiceResponse> processMessage(Message<BookingResponse> message) {
        return invoiceService.updateInvoiceAfterBookingUpdate(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnNext(invoiceResponse -> {
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                    log.info("Invoice updated: {}", invoiceResponse);
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing updated booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
