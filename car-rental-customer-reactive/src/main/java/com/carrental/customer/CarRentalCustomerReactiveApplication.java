package com.carrental.customer;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@CarRentalReactiveMicroservice
public class CarRentalCustomerReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalCustomerReactiveApplication.class, args);
    }

}
