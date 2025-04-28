package com.autohubreactive.agency.repository;

import com.autohubreactive.agency.migration.DatabaseCollectionCreator;
import com.autohubreactive.model.Car;
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

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@DataMongoTest
class CarRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    private final Car car1 = DatabaseCollectionCreator.getCars().getFirst();

    private final Car car2 = DatabaseCollectionCreator.getCars().getLast();

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void initCollection() {
        carRepository.deleteAll()
                .thenMany(carRepository.saveAll(DatabaseCollectionCreator.getCars()))
                .blockLast();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(mongoDbContainer.isCreated());
    }

    @Test
    void findByIdTest_success() {
        carRepository.findById(new ObjectId("65072052d5d4531e66a0c00c"))
                .as(StepVerifier::create)
                .assertNext(actualCar -> assertThat(actualCar).usingRecursiveComparison().isEqualTo(car1))
                .verifyComplete();
    }

    @Test
    void findAllByFilterInsensitiveCaseTest_success() {
        carRepository.findAllByFilterInsensitiveCase("Audi")
                .as(StepVerifier::create)
                .assertNext(actualCar -> assertThat(actualCar).usingRecursiveComparison().isEqualTo(car2))
                .verifyComplete();
    }

    @Test
    void findImageByCarIdTest_success() {
        carRepository.findImageByCarId(new ObjectId("65072052d5d4531e66a0c00c"))
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void findCarsByMakeInsensitiveCaseTest_success() {
        carRepository.findCarsByMakeInsensitiveCase("Volkswagen")
                .as(StepVerifier::create)
                .assertNext(actualCar -> assertThat(actualCar).usingRecursiveComparison().isEqualTo(car1))
                .verifyComplete();
    }

    @Test
    void findAllCarsTest_success() {
        carRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findAllAvailableCarsTest_success() {
        carRepository.findAllAvailableCars()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}
