package com.autohubreactive.booking.scheduler;

import com.autohubreactive.booking.service.outbox.UpdatedOutboxService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdatedOutboxScheduler {

    private final UpdatedOutboxService updatedOutboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    @SchedulerLock(
            name = "updatedOutboxSchedulerScheduledTask",
            lockAtLeastFor = "3s",
            lockAtMostFor = "10m"
    )
    public void pollOutboxCollection() {
        updatedOutboxService.handleOutboxes()
                .subscribe();
    }

}
