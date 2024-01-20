package com.swiftwheelshub.lib.repository;

import com.swiftwheelshub.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, ObjectId> {

    @Query("{ 'username': ?0 }")
    Mono<User> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);

    Mono<Void> deleteByUsername(String username);

}
