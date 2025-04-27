package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.expense.service.OutboxService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService outboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    @SchedulerLock(
            name = "invoiceSchedulerScheduledTask",
            lockAtLeastFor = "5s",
            lockAtMostFor = "30m"
    )
    public void pollOutboxCollection() {
        outboxService.handleOutboxes()
                .subscribe();
    }

}
