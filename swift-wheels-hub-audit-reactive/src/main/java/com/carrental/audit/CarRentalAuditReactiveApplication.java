package com.carrental.audit;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalReactiveMicroservice
public class CarRentalAuditReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalAuditReactiveApplication.class);
    }

}
