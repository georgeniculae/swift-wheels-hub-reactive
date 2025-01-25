package com.swiftwheelshubreactive.agency.repository;

import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CarRepository extends ReactiveMongoRepository<Car, ObjectId> {

    @Query(
            value = """
                    { 'id' : ?0 }""",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    @NonNull
    Mono<Car> findById(@NonNull ObjectId id);

    @Query(
            value = """
                    { 'id' : ?0 }""",
            fields = """
                    { 'image' : 1 }"""
    )
    @NonNull
    Mono<Car> findImageByCarId(@NonNull ObjectId id);

    @Query(
            value = """
                    {$and : [{ 'id' : ?0 }, { 'carStatus' : ?1 }]}""",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    @NonNull
    Mono<Car> findCarByIdAndCarStatus(ObjectId id, CarStatus carStatus);

    @Query(
            value = """
                    {$or : [{ 'make' : { $regex: '(?i)?0' } }, { 'model' : {$regex: '(?i)?0' } }]}""",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    Flux<Car> findAllByFilterInsensitiveCase(String filter);

    @Query(
            value = "{}",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    @NonNull
    Flux<Car> findAll();

    @Query(
            value = "{ 'carStatus' : 'AVAILABLE' }",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    @NonNull
    Flux<Car> findAllAvailableCars();

    @Query(
            value = """
                    { 'make' : { $regex: '(?i)?0' } }""",
            fields = """
                    {
                    'id' : 1, 'make' : 1, 'model' : 1, 'bodyType' : 1, 'yearOfProduction' : 1, 'color' : 1,
                    'mileage' : 1, 'carStatus' : 1, 'amount' : 1, 'originalBranch' : 1, 'actualBranch' : 1}"""
    )
    Flux<Car> findCarsByMakeInsensitiveCase(String make);

}
