package com.swiftwheelshubreactive.requestvalidator;

//import com.swiftwheelshub.lib.annotation.SwiftWheelsHubReactiveMicroservice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;

//@SwiftWheelsHubReactiveMicroservice
@SpringBootApplication
public class SwiftWheelsHubReactiveRequestValidatorApplication {

    public static void main(String[] args) {
        BlockHound.install();
        SpringApplication.run(SwiftWheelsHubReactiveRequestValidatorApplication.class, args);
    }

}
