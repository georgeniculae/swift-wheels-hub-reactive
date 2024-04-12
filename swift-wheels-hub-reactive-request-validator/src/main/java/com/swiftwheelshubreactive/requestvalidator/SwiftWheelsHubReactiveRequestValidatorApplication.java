package com.swiftwheelshubreactive.requestvalidator;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

import java.util.zip.InflaterInputStream;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveRequestValidatorApplication {

    public static void main(String[] args) {
        BlockHound.install(builder -> builder.allowBlockingCallsInside(InflaterInputStream.class.getName(), "read"));
        SpringApplication.run(SwiftWheelsHubReactiveRequestValidatorApplication.class, args);
    }

}
