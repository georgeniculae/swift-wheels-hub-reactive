package com.carrental.eurekaserivediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CarRentalEurekaServiceDiscoveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalEurekaServiceDiscoveryApplication.class, args);
    }

}
