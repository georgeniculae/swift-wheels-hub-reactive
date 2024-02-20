package com.swiftwheelshub.requestvalidator.config;

import com.swiftwheelshub.requestvalidator.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoadSwaggerCacheAtStartup {

    private final RedisService redisService;

    @EventListener(ApplicationStartedEvent.class)
    public void loadSwaggerFolderCache() {
        redisService.addSwaggerFolderToRedis()
                .subscribe();
    }

}
