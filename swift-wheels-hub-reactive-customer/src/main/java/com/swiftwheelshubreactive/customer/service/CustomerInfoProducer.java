package com.swiftwheelshubreactive.customer.service;

import com.swiftwheelshubreactive.dto.CustomerInfo;
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
public class CustomerInfoProducer {

    private final StreamBridge streamBridge;

    @Value("${spring.cloud.stream.bindings.customerInfoProducer-out-0.destination}")
    private String emailNotificationBinderName;

    @Value("${spring.cloud.stream.bindings.customerInfoProducer-out-0.contentType}")
    private String emailNotificationMimeType;

    public Mono<Void> sendInvoice(CustomerInfo customerInfo) {
        return Mono.fromRunnable(() -> streamBridge.send(emailNotificationBinderName, buildMessage(customerInfo), MimeType.valueOf(emailNotificationMimeType)))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    private Message<CustomerInfo> buildMessage(CustomerInfo customerInfo) {
        return MessageBuilder.withPayload(customerInfo)
                .build();
    }

}
