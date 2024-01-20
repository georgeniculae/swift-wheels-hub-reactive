package com.swiftwheelshub.agency;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalReactiveMicroservice
public class CarRentalAgencyReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalAgencyReactiveApplication.class, args);
    }

}
