package com.swiftwheelshub.audit;

import com.swiftwheelshub.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubAuditReactiveApplication {

    public static void main(String[] args) {
        BlockHound.install(builder -> builder.allowBlockingCallsInside("io.netty.util.concurrent.FastThreadLocalRunnable", "run"));
        SpringApplication.run(SwiftWheelsHubAuditReactiveApplication.class);
    }

}
