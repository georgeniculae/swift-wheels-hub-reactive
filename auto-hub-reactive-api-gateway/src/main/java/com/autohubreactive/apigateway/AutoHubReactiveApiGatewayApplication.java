package com.autohubreactive.apigateway;

import com.autohubreactive.lib.annotation.AutoHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;

@AutoHubReactiveMicroservice
public class AutoHubReactiveApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHubReactiveApiGatewayApplication.class, args);
    }

}
