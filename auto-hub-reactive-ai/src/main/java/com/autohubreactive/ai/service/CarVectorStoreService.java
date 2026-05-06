package com.autohubreactive.ai.service;

import com.autohubreactive.dto.ai.AvailableCarDetails;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarVectorStoreService {

    private final VectorStore vectorStore;

    public Mono<Void> addCar(AvailableCarDetails car) {
        return Mono.fromRunnable(() -> vectorStore.add(List.of(buildDocument(car))))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(_ -> log.info("Car with id {} added to vector store", car.id()))
                .onErrorMap(e -> {
                    log.error("Error adding car {} to vector store: {}", car.id(), e.getMessage());

                    return ExceptionUtil.handleException(e);
                })
                .then();
    }

    public Mono<Void> deleteCar(String carId) {
        return Mono.fromRunnable(() -> vectorStore.delete(List.of(carId)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(_ -> log.info("Car with id {} removed from vector store", carId))
                .onErrorMap(e -> {
                    log.error("Error removing car {} from vector store: {}", carId, e.getMessage());
                    return ExceptionUtil.handleException(e);
                })
                .then();
    }

    public Mono<List<Document>> searchSimilarCars(String queryText, int topK) {
        return Mono.fromCallable(() -> vectorStore.similaritySearch(getSearchRequest(queryText, topK)))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error searching vector store: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private SearchRequest getSearchRequest(String queryText, int topK) {
        return SearchRequest.builder()
                .query(queryText)
                .topK(topK)
                .build();
    }

    private Document buildDocument(AvailableCarDetails car) {
        return Document.builder()
                .id(car.id())
                .text(buildCarText(car))
                .metadata(Map.of(
                        "carId", car.id(),
                        "make", car.make(),
                        "model", car.model(),
                        "bodyCategory", car.bodyCategory().name(),
                        "yearOfProduction", car.yearOfProduction(),
                        "color", car.color(),
                        "amount", car.amount().toString(),
                        "carLocation", car.carLocation()
                ))
                .build();
    }

    private String buildCarText(AvailableCarDetails car) {
        return String.format("%s %s %s from %d, %s, %d km, price %s per day, located in %s",
                car.make(),
                car.model(),
                car.bodyCategory().getDisplayName(),
                car.yearOfProduction(),
                car.color(),
                car.mileage(),
                car.amount(),
                car.carLocation());
    }

}