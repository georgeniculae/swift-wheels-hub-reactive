package com.swiftwheelshub.emailnotification.consumer;

import com.carrental.dto.InvoiceDto;
import com.swiftwheelshub.emailnotification.service.EmailService;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
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
    public Function<Flux<Message<InvoiceDto>>, Mono<Void>> emailNotificationConsumer() {
        return messageFlux -> messageFlux.concatMap(this::processMessage)
                .then();
    }

    private Mono<Response> processMessage(Message<InvoiceDto> message) {
        return Mono.just(message.getPayload())
                .map(invoiceDto -> {
                    Mail mail = emailService.createMail(invoiceDto.getCustomerEmail(), invoiceDto);

                    return emailService.sendEmail(mail);
                })
                .doOnNext(userDto -> {
                    log.info("Booking saved: {}", userDto);

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
