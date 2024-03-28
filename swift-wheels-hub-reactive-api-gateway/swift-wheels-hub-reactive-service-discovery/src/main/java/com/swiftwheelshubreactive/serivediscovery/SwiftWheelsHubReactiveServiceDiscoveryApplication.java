package com.swiftwheelshubreactive.serivediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SwiftWheelsHubReactiveServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftWheelsHubReactiveServiceDiscoveryApplication.class, args);
    }

}
