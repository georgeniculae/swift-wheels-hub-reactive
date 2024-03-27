package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.agency.migration.DatabaseCollectionCreator;
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

import static com.mongodb.assertions.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers
@DataMongoTest
class RentalOfficeRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private RentalOfficeRepository rentalOfficeRepository;

    @BeforeEach
    void initCollection() {
        rentalOfficeRepository.deleteAll()
                .thenMany(rentalOfficeRepository.saveAll(DatabaseCollectionCreator.getRentalOffices()))
                .blockLast();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(mongoDbContainer.isCreated());
    }

    @Test
    void findAllByFilterInsensitiveCaseTest_success() {
        rentalOfficeRepository.findAllByFilterInsensitiveCase("Rental Office")
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}
