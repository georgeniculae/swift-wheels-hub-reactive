package com.carrental.customer;

import com.swiftwheelshub.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@SwiftWheelsHubReactiveMicroservice
public class CarRentalCustomerReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalCustomerReactiveApplication.class, args);
    }

}
