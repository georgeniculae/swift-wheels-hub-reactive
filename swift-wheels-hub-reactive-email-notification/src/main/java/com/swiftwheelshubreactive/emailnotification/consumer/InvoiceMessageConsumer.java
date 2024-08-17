package com.swiftwheelshubreactive.emailnotification.consumer;

import com.sendgrid.Response;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.emailnotification.service.EmailService;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class InvoiceMessageConsumer {

    @Value("${invoiceProducer.isMessageAckEnabled:false}")
    private boolean isMessageAckEnabled;

    private final EmailService emailService;

    @Bean
    public Function<Flux<Message<InvoiceResponse>>, Mono<Void>> emailNotificationConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<Response> processMessage(Message<InvoiceResponse> message) {
        return Mono.just(message.getPayload())
                .flatMap(invoiceResponse -> emailService.sendEmail(invoiceResponse.customerEmail(), invoiceResponse))
                .doOnNext(response -> {
                    log.info("Invoice processing status: {}{}", response.getStatusCode(), response.getBody());

                    if (isMessageAckEnabled) {
                        sendMessageAcknowledgement(message.getHeaders());
                    }
                })
                .onErrorResume(e -> {
                    log.error("Exception during processing saved booking message: {}", e.getMessage(), e);

                    return Mono.empty();
                });
    }

    private void sendMessageAcknowledgement(MessageHeaders messageHeaders) {
        Optional.ofNullable(messageHeaders.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class))
                .orElseThrow(
                        () -> new SwiftWheelsHubResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "There is no Kafka acknowledgement in message headers"
                        )
                )
                .acknowledge();
    }

}
