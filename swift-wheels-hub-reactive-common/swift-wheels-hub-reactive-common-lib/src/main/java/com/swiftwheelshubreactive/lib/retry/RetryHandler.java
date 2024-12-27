package com.swiftwheelshubreactive.lib.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Component
@Slf4j
public class RetryHandler {

    public RetryBackoffSpec retry() {
        return Retry.backoff(3, Duration.ofSeconds(10))
                .doBeforeRetry(retrySignal -> log.warn(retrySignal.failure().getMessage()));
    }

}
