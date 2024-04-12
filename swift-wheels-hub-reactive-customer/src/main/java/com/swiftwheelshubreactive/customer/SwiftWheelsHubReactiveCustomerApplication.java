package com.swiftwheelshubreactive.customer;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

import java.util.zip.InflaterInputStream;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveCustomerApplication {

    public static void main(String[] args) {
        BlockHound.install(builder -> builder.allowBlockingCallsInside(InflaterInputStream.class.getName(), "read"));
        SpringApplication.run(SwiftWheelsHubReactiveCustomerApplication.class, args);
    }

}
