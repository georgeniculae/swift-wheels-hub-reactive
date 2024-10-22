package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.expense.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    public void pollOutboxCollection() {
        outboxService.handleOutboxes()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
