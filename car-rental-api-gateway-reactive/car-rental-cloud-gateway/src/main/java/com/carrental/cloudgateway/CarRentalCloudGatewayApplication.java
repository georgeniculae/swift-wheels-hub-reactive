package com.carrental.cloudgateway;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalReactiveMicroservice
public class CarRentalCloudGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalCloudGatewayApplication.class, args);
    }

}
