package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.model.RentalOffice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RentalOfficeRepository extends ReactiveMongoRepository<RentalOffice, ObjectId> {

    @Query("{'name': {$regex: '(?i)?0'}}")
    Flux<RentalOffice> findAllByNameInsensitiveCase(String rentalOfficeName);

}
