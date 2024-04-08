package com.swiftwheelshubreactive.agency;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
//import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveAgencyApplication {

    public static void main(String[] args) {
//        BlockHound.install(builder -> builder.allowBlockingCallsInside("io.netty.util.concurrent.FastThreadLocalRunnable", "run"));
        SpringApplication.run(SwiftWheelsHubReactiveAgencyApplication.class, args);
    }

}
