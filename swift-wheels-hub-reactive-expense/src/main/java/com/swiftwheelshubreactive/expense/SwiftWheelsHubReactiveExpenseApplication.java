package com.swiftwheelshubreactive.expense;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import reactor.blockhound.BlockHound;

import java.util.zip.InflaterInputStream;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveExpenseApplication {

    public static void main(String[] args) {
        BlockHound.install(builder -> builder.allowBlockingCallsInside(InflaterInputStream.class.getName(), "read"));
        SpringApplication.run(SwiftWheelsHubReactiveExpenseApplication.class, args);
    }

}
