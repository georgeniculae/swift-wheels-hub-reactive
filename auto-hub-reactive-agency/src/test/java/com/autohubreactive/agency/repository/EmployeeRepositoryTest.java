package com.autohubreactive.agency.repository;

import com.autohubreactive.agency.migration.DatabaseCollectionCreator;
import com.autohubreactive.model.Employee;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@DataMongoTest
class EmployeeRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    private final Employee employee1 = DatabaseCollectionCreator.getEmployees().getFirst();

    private final Employee employee2 = DatabaseCollectionCreator.getEmployees().getLast();

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void initCollection() {
        employeeRepository.deleteAll()
                .thenMany(employeeRepository.saveAll(List.of(employee1, employee2)))
                .blockLast();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(mongoDbContainer.isCreated());
    }

    @Test
    void findAllByFilterInsensitiveCaseTest_success() {
        employeeRepository.findAllByFilterInsensitiveCase("manager")
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findAllEmployeesByBranchIdTest_success() {
        employeeRepository.findAllEmployeesByBranchId(new ObjectId("65072051d5d4531e66a0c00b"))
                .as(StepVerifier::create)
                .assertNext(employee -> assertThat(employee).usingRecursiveComparison().isEqualTo(employee2))
                .verifyComplete();
    }

    @Test
    void deleteByBranchIdTest_success() {
        employeeRepository.deleteByBranchId(new ObjectId("65072051d5d4531e66a0c00a"))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
