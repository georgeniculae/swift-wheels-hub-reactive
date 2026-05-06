package com.autohubreactive.agency.testconfig;

import com.autohubreactive.agency.repository.BranchRepository;
import com.autohubreactive.agency.repository.CarRepository;
import com.autohubreactive.agency.repository.EmployeeRepository;
import com.autohubreactive.agency.repository.RentalOfficeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.mongodb.MongoDBContainer;

@ActiveProfiles("test")
@DataMongoTest
public class MongoTestcontainers {

    protected static final MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    static {
        mongoDbContainer.start();
    }

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDbContainer::getConnectionString);
    }

    @Autowired
    protected BranchRepository branchRepository;

    @Autowired
    protected CarRepository carRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected RentalOfficeRepository rentalOfficeRepository;

    @MockitoBean
    protected StreamBridge streamBridge;

}
