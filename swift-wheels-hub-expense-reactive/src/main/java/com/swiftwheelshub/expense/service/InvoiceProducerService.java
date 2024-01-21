package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.InvoiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class InvoiceProducerService {

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.destination}")
    private String emailNotificationBinderName;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.contentType}")
    private String emailNotificationMimeType;

    private final StreamBridge streamBridge;

    public Mono<Boolean> sendInvoice(InvoiceDto invoiceDto) {
        return Mono.fromCallable(() -> streamBridge.send(emailNotificationBinderName, buildMessage(invoiceDto), MimeType.valueOf(emailNotificationMimeType)))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Message<InvoiceDto> buildMessage(InvoiceDto invoiceDto) {
        return MessageBuilder.withPayload(invoiceDto)
                .build();
    }

}
