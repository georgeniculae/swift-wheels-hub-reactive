package com.swiftwheelshubreactive.eurekaserivediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SwiftWheelsHubReactiveEurekaServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftWheelsHubReactiveEurekaServiceDiscoveryApplication.class, args);
    }

}
