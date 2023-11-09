package com.carrental.booking;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@CarRentalReactiveMicroservice
public class CarRentalBookingReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalBookingReactiveApplication.class, args);
    }

}
