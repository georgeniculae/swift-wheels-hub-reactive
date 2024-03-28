package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    public void pollOutboxCollection() {
        outboxService.handleOutboxes()
                .subscribe();
    }

}
