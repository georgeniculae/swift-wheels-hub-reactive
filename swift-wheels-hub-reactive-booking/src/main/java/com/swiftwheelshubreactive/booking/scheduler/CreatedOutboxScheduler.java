package com.swiftwheelshubreactive.booking.scheduler;

import com.swiftwheelshubreactive.booking.service.outbox.CreatedOutboxService;
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
            lockAtLeastFor = "30s",
            lockAtMostFor = "1m"
    )
    public void pollOutboxCollection() {
        createdOutboxService.handleOutboxes()
                .subscribe();
    }

}
