package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.InvoiceResponse;
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

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.destination}")
    private String emailNotificationBinderName;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.contentType}")
    private String emailNotificationMimeType;

    public Mono<Boolean> sendInvoice(InvoiceResponse invoiceResponse) {
        return Mono.fromCallable(() -> streamBridge.send(emailNotificationBinderName, buildMessage(invoiceResponse), MimeType.valueOf(emailNotificationMimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<InvoiceResponse> buildMessage(InvoiceResponse invoiceResponse) {
        return MessageBuilder.withPayload(invoiceResponse)
                .build();
    }

}
