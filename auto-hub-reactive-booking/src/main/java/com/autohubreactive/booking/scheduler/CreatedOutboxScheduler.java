package com.autohubreactive.booking.scheduler;

import com.autohubreactive.booking.service.outbox.CreatedOutboxService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreatedOutboxScheduler {

    private final CreatedOutboxService createdOutboxService;

    @Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
    @SchedulerLock(
            name = "createdOutboxSchedulerScheduledTask",
            lockAtLeastFor = "3s",
            lockAtMostFor = "10m"
    )
    public void pollOutboxCollection() {
        createdOutboxService.handleOutboxes()
                .subscribe();
    }

}
