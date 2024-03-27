package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.model.Branch;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository extends ReactiveMongoRepository<Branch, ObjectId> {

    @Query("""
            {$or : [
            { 'name': { $regex: '(?i)?0' } },
            { 'address': { $regex: '(?i)?0' } },
            { 'rentalOffice.name': { $regex: '(?i)?0' } }
            ]}""")
    Flux<Branch> findAllByFilterInsensitiveCase(String filter);

    @Query(
            value = """
                    {'rentalOffice.id' : $0}""",
            delete = true
    )
    Mono<Void> deleteByRentalOfficeId(ObjectId id);

}
