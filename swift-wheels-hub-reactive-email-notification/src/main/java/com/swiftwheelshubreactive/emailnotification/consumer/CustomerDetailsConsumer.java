package com.swiftwheelshubreactive.emailnotification.consumer;

import com.swiftwheelshubreactive.dto.CustomerInfo;
import com.swiftwheelshubreactive.emailnotification.service.CustomerDetailsService;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerDetailsConsumer {

    private final CustomerDetailsService customerDetailsService;

    @Value("${invoiceProducer.isMessageAckEnabled:false}")
    private boolean isMessageAckEnabled;

    @Bean
    public Function<Flux<Message<CustomerInfo>>, Mono<Void>> customerInfoConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<Void> processMessage(Message<CustomerInfo> message) {
        return Mono.just(message.getPayload())
                .flatMap(customerDetailsService::saveCustomerDetails)
                .doOnSuccess(_ -> {
                    log.info("Customer details saved for username: {}", message.getPayload().username());

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
