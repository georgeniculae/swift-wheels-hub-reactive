package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.service.outbox.DeletedOutboxService;
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
            lockAtLeastFor = "5s",
            lockAtMostFor = "30m"
    )
    public void pollOutboxCollection() {
        deletedOutboxService.handleOutboxes()
                .subscribe();
    }

}
