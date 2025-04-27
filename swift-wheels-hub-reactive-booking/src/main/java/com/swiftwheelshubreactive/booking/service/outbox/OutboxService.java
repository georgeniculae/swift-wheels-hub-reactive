package com.swiftwheelshubreactive.booking.service.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public abstract class OutboxService {

    private final ReactiveRedisOperations<String, String> redisOperations;

    public abstract Flux<Void> handleOutboxes();

    protected Mono<Void> unlockCar(String carId) {
        return redisOperations.opsForValue()
                .delete(carId)
                .then();
    }

}
