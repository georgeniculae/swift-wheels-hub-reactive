package com.swiftwheelshubreactive.emailnotification;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
//import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveEmailNotificationApplication {

    public static void main(String[] args) {
//        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubReactiveEmailNotificationApplication.class);
    }

}
