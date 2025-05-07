package com.autohubreactive.expense;

import com.autohubreactive.lib.annotation.AutoHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubReactiveMicroservice
public class AutoHubReactiveExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHubReactiveExpenseApplication.class, args);
    }

}
