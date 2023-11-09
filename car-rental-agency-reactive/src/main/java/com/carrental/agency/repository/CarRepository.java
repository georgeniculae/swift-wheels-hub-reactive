package com.carrental.agency.repository;

import com.carrental.document.model.Car;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface CarRepository extends ReactiveMongoRepository<Car, ObjectId> {

    @Query("{$or : [{'make' : {$regex: '(?i)?0'}}, {'model' : {$regex: '(?i)?0'}}]}")
    Flux<Car> findAllByFilterInsensitiveCase(String filter);

    Flux<Car> findCarsByMake(String make);

}
