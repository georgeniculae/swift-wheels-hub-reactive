package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.agency.migration.DatabaseCollectionCreator;
import com.swiftwheelshubreactive.model.Car;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
@Testcontainers
@DataMongoTest
class CarRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer =
            new MongoDBContainer("mongo:latest")
                    .withExposedPorts(27017)
                    .withReuse(true);

    @Autowired
    private CarRepository carRepository;

    private final Car car1 = DatabaseCollectionCreator.getCars().getFirst();

    private final Car car2 = DatabaseCollectionCreator.getCars().getLast();

    @BeforeAll
    public static void start() {
        mongoDbContainer.start();
    }

    @BeforeEach
    public void initDb() {
        carRepository.deleteAll().subscribe();

        carRepository.save(car1).subscribe();
        carRepository.save(car2).subscribe();
    }

    @AfterEach
    public void eraseDb() {
        carRepository.deleteAll().subscribe();
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
    void findAllCarsTest_success() {
        carRepository.findAll()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();
    }

}
