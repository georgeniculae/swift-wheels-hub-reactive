package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.model.RentalOffice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RentalOfficeRepository extends ReactiveMongoRepository<RentalOffice, ObjectId> {

    @Query("""
            {$or : [{'name': {$regex: '(?i)?0'}}, {'address': {$regex: '(?i)?0'}}, {'phoneNumber': {$regex: '(?i)?0'}}]}""")
    Flux<RentalOffice> findAllByFilterInsensitiveCase(String filter);

}
