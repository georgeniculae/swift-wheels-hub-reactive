package com.swiftwheelshub.requestvalidator;

import com.swiftwheelshub.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubRequestValidatorReactiveApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubRequestValidatorReactiveApplication.class, args);
    }

}
