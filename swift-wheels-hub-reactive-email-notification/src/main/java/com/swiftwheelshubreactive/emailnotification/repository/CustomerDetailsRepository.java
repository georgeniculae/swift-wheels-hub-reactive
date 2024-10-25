package com.swiftwheelshubreactive.emailnotification.repository;

import com.swiftwheelshubreactive.emailnotification.model.CustomerDetails;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CustomerDetailsRepository extends ReactiveMongoRepository<CustomerDetails, ObjectId> {

    Mono<CustomerDetails> findByUsername(String username);

}
