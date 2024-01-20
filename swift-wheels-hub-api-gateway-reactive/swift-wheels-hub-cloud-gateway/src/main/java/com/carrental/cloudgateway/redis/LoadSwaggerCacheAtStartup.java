package com.carrental.cloudgateway.redis;

import com.carrental.cloudgateway.service.RedisService;
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
