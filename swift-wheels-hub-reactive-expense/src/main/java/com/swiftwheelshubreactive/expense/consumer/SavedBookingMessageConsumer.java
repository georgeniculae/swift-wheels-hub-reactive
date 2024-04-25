package com.swiftwheelshubreactive.expense.consumer;

import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
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
public class SavedBookingMessageConsumer {

    @Value("${bookingConsumer.isMessageAckEnabled:false}")
    private boolean isMessageAckEnabled;

    private final InvoiceService invoiceService;

    @Bean
    public Function<Flux<Message<BookingResponse>>, Mono<Void>> savedBookingConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<InvoiceResponse> processMessage(Message<BookingResponse> message) {
        return invoiceService.saveInvoice(message.getPayload())
                .doOnNext(invoiceResponse -> {
                    log.info("Invoice saved: {}", invoiceResponse);

                    if (isMessageAckEnabled) {
                        this.sendMessageAcknowledgement(message.getHeaders());
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
