package com.swiftwheelshubreactive.expense;

import com.swiftwheelshubreactive.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
//import reactor.blockhound.BlockHound;

@SwiftWheelsHubReactiveMicroservice
public class SwiftWheelsHubReactiveExpenseApplication {

    public static void main(String[] args) {
//        BlockHound.install(builder -> builder.allowBlockingCallsInside("io.netty.util.concurrent.FastThreadLocalRunnable", "run"));
        SpringApplication.run(SwiftWheelsHubReactiveExpenseApplication.class, args);
    }

}
