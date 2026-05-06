package com.autohubreactive.agency.repository;

import com.autohubreactive.agency.entity.RentalOffice;
import com.autohubreactive.agency.testconfig.MongoTestcontainers;
import com.autohubreactive.agency.util.TestUtil;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static com.mongodb.assertions.Assertions.assertTrue;

class RentalOfficeRepositoryTest extends MongoTestcontainers {

    private static final RentalOffice RENTAL_OFFICE_1 = TestUtil.getResourceAsJson("/data/RentalOffice1.json", RentalOffice.class);
    private static final RentalOffice RENTAL_OFFICE_2 = TestUtil.getResourceAsJson("/data/RentalOffice2.json", RentalOffice.class);

    @BeforeEach
    void initCollection() {
        rentalOfficeRepository.deleteAll()
                .thenMany(rentalOfficeRepository.saveAll(List.of(RENTAL_OFFICE_1, RENTAL_OFFICE_2)))
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

    @Test
    void deleteByRentalOfficeIdTest_success() {
        rentalOfficeRepository.deleteByBranchId(new ObjectId("65072050d5d4531e66a0c008"))
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

}
