package com.autohubreactive.emailnotification.consumer;

import com.autohubreactive.dto.emailnotification.EmailResponse;
import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.emailnotification.service.EmailProcessorService;
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
public class InvoiceMessageConsumer {

    private final EmailProcessorService emailProcessorService;
    private final RetryHandler retryHandler;

    @Bean
    public Function<Flux<Message<InvoiceResponse>>, Mono<Void>> emailNotificationConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<EmailResponse> processMessage(Message<InvoiceResponse> message) {
        return emailProcessorService.sendEmail(message.getPayload())
                .retryWhen(retryHandler.retry())
                .doOnNext(response -> {
                    log.info("Invoice processed with status: {}{}", response.statusCode(), response.body());
                    KafkaUtil.acknowledgeMessage(message.getHeaders());
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

}
