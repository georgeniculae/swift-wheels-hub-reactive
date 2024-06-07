package com.swiftwheelshub.ai;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveAiApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubReactiveAiApplication.class, args);
    }

}
