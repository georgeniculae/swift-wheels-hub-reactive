package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.model.Branch;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BranchRepository extends ReactiveMongoRepository<Branch, ObjectId> {

    @Query("{$or : [{'name': {$regex: '(?i)?0'}}, {'rentalOffice.name': {$regex: '(?i)?0'}}]}")
    Flux<Branch> findAllByFilterInsensitiveCase(String filter);

}