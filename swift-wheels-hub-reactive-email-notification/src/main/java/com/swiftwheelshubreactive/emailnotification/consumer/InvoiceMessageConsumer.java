package com.swiftwheelshubreactive.emailnotification.consumer;

import com.swiftwheelshubreactive.dto.EmailResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.emailnotification.service.EmailProcessorService;
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
