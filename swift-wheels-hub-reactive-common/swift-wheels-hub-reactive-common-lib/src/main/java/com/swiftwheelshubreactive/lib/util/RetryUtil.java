package com.swiftwheelshubreactive.lib.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@UtilityClass
@Slf4j
public class RetryUtil {

    public static RetryBackoffSpec retry() {
        return Retry.backoff(3, Duration.ofSeconds(10))
                .doBeforeRetry(retrySignal -> log.warn(retrySignal.failure().getMessage()));
    }

}
