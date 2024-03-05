package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CarHandler {

    private static final String ID = "id";
    private static final String MAKE = "make";
    private static final String FILTER = "filter";
    private static final String FILE = "file";
    private final CarService carService;

    public Mono<ServerResponse> findAllCars(ServerRequest serverRequest) {
        return carService.findAllCars()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(carResponses -> ServerResponse.ok().bodyValue(carResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findCarById(ServerRequest serverRequest) {
        return carService.findCarById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findCarsByMake(ServerRequest serverRequest) {
        return carService.findCarsByMake(ServerRequestUtil.getPathVariable(serverRequest, MAKE))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(carResponses -> ServerResponse.ok().bodyValue(carResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findCarsByFilterInsensitiveCase(ServerRequest serverRequest) {
        return carService.findCarsByFilterInsensitiveCase(ServerRequestUtil.getPathVariable(serverRequest, FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(carResponses -> ServerResponse.ok().bodyValue(carResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAvailableCar(ServerRequest serverRequest) {
        return carService.getAvailableCar(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse));
    }

    public Mono<ServerResponse> countCars(ServerRequest serverRequest) {
        return carService.countCars()
                .flatMap(numberOfCars -> ServerResponse.ok().bodyValue(numberOfCars));
    }

    public Mono<ServerResponse> saveCar(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CarRequest.class)
                .flatMap(carService::saveCar)
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> uploadCars(ServerRequest serverRequest) {
        return serverRequest.multipartData()
                .map(multiValueMap -> multiValueMap.get(FILE))
                .flatMapMany(Flux::fromIterable)
                .cast(FilePart.class)
                .flatMap(carService::uploadCars)
                .collectList()
                .flatMap(carResponses -> ServerResponse.ok().bodyValue(carResponses));
    }

    public Mono<ServerResponse> updateCar(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CarRequest.class)
                .flatMap(carRequest -> carService.updateCar(ServerRequestUtil.getPathVariable(serverRequest, ID), carRequest))
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse));
    }

    public Mono<ServerResponse> updateCarStatus(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CarState.class)
                .flatMap(carStatus -> carService.updateCarStatus(ServerRequestUtil.getPathVariable(serverRequest, ID), carStatus))
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse));
    }

    public Mono<ServerResponse> updateCarWhenBookingIsClosed(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CarUpdateDetails.class)
                .flatMap(carUpdateDetails -> carService.updateCarWhenBookingIsClosed(ServerRequestUtil.getPathVariable(serverRequest, ID), carUpdateDetails))
                .flatMap(carResponse -> ServerResponse.ok().bodyValue(carResponse));
    }

    public Mono<ServerResponse> updateCarsStatus(ServerRequest serverRequest) {
        return serverRequest.bodyToFlux(UpdateCarRequest.class)
                .flatMap(updateCarRequest -> carService.updateCarStatus(updateCarRequest.carId(), updateCarRequest.carState()))
                .collectList()
                .flatMap(carResponses -> ServerResponse.ok().bodyValue(carResponses));
    }

    public Mono<ServerResponse> deleteCarById(ServerRequest serverRequest) {
        return carService.deleteCarById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.noContent().build());
    }

}
