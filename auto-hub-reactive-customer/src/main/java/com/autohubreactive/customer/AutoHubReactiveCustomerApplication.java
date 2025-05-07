package com.autohubreactive.customer;

import com.autohubreactive.lib.annotation.AutoHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubReactiveMicroservice
public class AutoHubReactiveCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHubReactiveCustomerApplication.class, args);
    }

}
