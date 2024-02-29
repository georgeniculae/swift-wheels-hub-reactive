package com.swiftwheelshubreactive.requestvalidator;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveRequestValidatorApplication {

    public static void main(String[] args) {
//        BlockHound.install(builder -> builder.allowBlockingCallsInside("jdk.internal.misc.Unsafe", "park"));
        SpringApplication.run(SwiftWheelsHubReactiveRequestValidatorApplication.class, args);
    }

}
