package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.agency.migration.DatabaseCollectionCreator;
import com.swiftwheelshubreactive.model.Branch;
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
class BranchRepositoryTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDbContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    private BranchRepository branchRepository;

    private final Branch branch1 = DatabaseCollectionCreator.getBranches().getFirst();

    private final Branch branch2 = DatabaseCollectionCreator.getBranches().getLast();

    @BeforeEach
    void initCollection() {
        branchRepository.deleteAll()
                .thenMany(branchRepository.saveAll(DatabaseCollectionCreator.getBranches()))
                .blockLast();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(mongoDbContainer.isCreated());
    }

    @Test
    void findAllByFilterInsensitiveCaseTest_success() {
        branchRepository.findAllByFilterInsensitiveCase("Branch")
                .as(StepVerifier::create)
                .assertNext(branch -> assertThat(branch).usingRecursiveComparison().isEqualTo(branch1))
                .assertNext(branch -> assertThat(branch).usingRecursiveComparison().isEqualTo(branch2))
                .verifyComplete();
    }

    @Test
    void deleteByRentalOfficeIdTest_success() {
        branchRepository.deleteByRentalOfficeId(new ObjectId("65072050d5d4531e66a0c008"))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
