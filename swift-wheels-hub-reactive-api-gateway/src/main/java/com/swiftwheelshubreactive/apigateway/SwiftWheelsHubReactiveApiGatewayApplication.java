package com.swiftwheelshubreactive.apigateway;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

import java.util.zip.InflaterInputStream;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveApiGatewayApplication {

    public static void main(String[] args) {
        BlockHound.install(builder -> builder.allowBlockingCallsInside(InflaterInputStream.class.getName(), "read"));
        SpringApplication.run(SwiftWheelsHubReactiveApiGatewayApplication.class, args);
    }

}
