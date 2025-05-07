package com.autohubreactive.agency.repository;

import com.autohubreactive.model.agency.Employee;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EmployeeRepository extends ReactiveMongoRepository<Employee, ObjectId> {

    @Query("""
            {$or : [
            { 'firstName': {$regex: '(?i)?0' } },
            { 'lastName': {$regex: '(?i)?0' } },
            { 'jobPosition': {$regex: '(?i)?0' } },
            { 'workingBranch.name': {$regex: '(?i)?0' } },
            ]}""")
    Flux<Employee> findAllByFilterInsensitiveCase(String filter);

    @Query("""
            {'workingBranch.id': ?0}""")
    Flux<Employee> findAllEmployeesByBranchId(ObjectId id);

    @Query(
            value = """
                    {'branch.id' : $0}""",
            delete = true
    )
    Mono<Void> deleteByBranchId(ObjectId id);

}
