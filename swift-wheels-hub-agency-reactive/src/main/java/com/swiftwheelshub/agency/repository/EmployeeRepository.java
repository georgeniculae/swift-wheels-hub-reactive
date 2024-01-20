package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.model.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, ObjectId> {

    @Query("{$or : [{'firstName': {$regex: '(?i)?0'}}, {'lastName': {$regex: '(?i)?0'}}]}")
    Flux<Employee> findAllByFilterInsensitiveCase(String filter);

    @Query("{'workingBranch.id': ?0}")
    Flux<Employee> findAllEmployeesByBranchId(ObjectId id);

}
