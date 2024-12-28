package com.swiftwheelshubreactive.expense.producer;

import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class InvoiceProducerService {

    private final StreamBridge streamBridge;
    private final RetryHandler retryHandler;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.destination}")
    private String binderName;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.contentType}")
    private String mimeType;

    public Mono<Boolean> sendInvoice(InvoiceResponse invoiceResponse) {
        return Mono.fromCallable(
                        () -> streamBridge.send(
                                binderName,
                                buildMessage(invoiceResponse),
                                MimeType.valueOf(mimeType)
                        )
                )
                .subscribeOn(Schedulers.boundedElastic())
                .retryWhen(retryHandler.retry());
    }

    private Message<InvoiceResponse> buildMessage(InvoiceResponse invoiceResponse) {
        return MessageBuilder.withPayload(invoiceResponse)
                .build();
    }

}
