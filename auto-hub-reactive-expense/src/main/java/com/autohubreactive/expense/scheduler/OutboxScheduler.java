package com.autohubreactive.expense.scheduler;

import com.autohubreactive.expense.service.OutboxService;
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
            lockAtLeastFor = "3s",
            lockAtMostFor = "10m"
    )
    public void pollOutboxCollection() {
        outboxService.handleOutboxes()
                .subscribe();
    }

}
