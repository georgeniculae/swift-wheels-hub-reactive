package com.swiftwheelshubreactive.lib.util;

import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.Optional;

@UtilityClass
@Slf4j
public class KafkaUtil {

    public void acknowledgeMessage(MessageHeaders messageHeaders) {
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
