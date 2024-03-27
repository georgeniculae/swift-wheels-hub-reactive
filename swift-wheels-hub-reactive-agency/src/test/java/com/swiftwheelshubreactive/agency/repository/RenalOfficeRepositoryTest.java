package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.agency.migration.DatabaseCollectionCreator;
import com.swiftwheelshubreactive.model.RentalOffice;
import org.junit.jupiter.api.AfterEach;
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
class RenalOfficeRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private RentalOfficeRepository rentalOfficeRepository;

    private final RentalOffice rentalOffice1 = DatabaseCollectionCreator.getRentalOffices().getFirst();

    private final RentalOffice rentalOffice2 = DatabaseCollectionCreator.getRentalOffices().getLast();

    @BeforeEach
    void initCollection() {
        rentalOfficeRepository.deleteAll().block();
        rentalOfficeRepository.save(rentalOffice1).block();
        rentalOfficeRepository.save(rentalOffice2).block();
    }

    @AfterEach
    void eraseCollection() {
        rentalOfficeRepository.deleteAll().block();
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
