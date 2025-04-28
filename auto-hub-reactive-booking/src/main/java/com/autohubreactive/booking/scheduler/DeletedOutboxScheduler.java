package com.autohubreactive.booking.scheduler;

import com.autohubreactive.booking.service.outbox.DeletedOutboxService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeletedOutboxScheduler {

    private final DeletedOutboxService deletedOutboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    @SchedulerLock(
            name = "deletedOutboxSchedulerScheduledTask",
            lockAtLeastFor = "3s",
            lockAtMostFor = "10m"
    )
    public void pollOutboxCollection() {
        deletedOutboxService.handleOutboxes()
                .subscribe();
    }

}
