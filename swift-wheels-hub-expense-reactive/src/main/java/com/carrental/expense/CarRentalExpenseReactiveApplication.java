package com.carrental.expense;

import com.carrental.lib.annotation.CarRentalReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@CarRentalReactiveMicroservice
public class CarRentalExpenseReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalExpenseReactiveApplication.class, args);
    }

}
