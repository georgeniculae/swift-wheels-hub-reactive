package com.swiftwheelshub.customer;

import com.swiftwheelshub.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubCustomerReactiveApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubCustomerReactiveApplication.class, args);
    }

}