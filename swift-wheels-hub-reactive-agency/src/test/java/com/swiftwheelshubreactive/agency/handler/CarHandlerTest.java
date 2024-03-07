package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.agency.util.TestUtils;
import com.swiftwheelshubreactive.agency.validator.CarRequestValidator;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarHandlerTest {

    @InjectMocks
    private CarHandler carHandler;

    @Mock
    private CarService carService;

    @Mock
    private FilePart filePart;

    @Mock
    private CarRequestValidator carRequestValidator;

    @Test
    void findAllCarsTest_success() {
        CarResponse carResponse =
                TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.findAllCars()).thenReturn(Flux.fromIterable(carDtoList));

        StepVerifier.create(carHandler.findAllCars(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllCarsTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.findAllCars()).thenReturn(Flux.empty());

        StepVerifier.create(carHandler.findAllCars(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarByIdTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.findCarById(anyString())).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.findCarById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findCarByIdTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.findCarById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(carHandler.findCarById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarByMakeTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("make", "Volkswagen")
                .build();

        when(carService.findCarsByMake(anyString())).thenReturn(Flux.fromIterable(carDtoList));

        StepVerifier.create(carHandler.findCarsByMake(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findCarByMakeTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("make", "Volkswagen")
                .build();

        when(carService.findCarsByMake(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(carHandler.findCarsByMake(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarsByFilterTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Volkswagen")
                .build();

        when(carService.findCarsByFilterInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(carDtoList));

        StepVerifier.create(carHandler.findCarsByFilterInsensitiveCase(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findCarsByFilterTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Volkswagen")
                .build();

        when(carService.findCarsByFilterInsensitiveCase(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(carHandler.findCarsByFilterInsensitiveCase(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getAvailableCarTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.getAvailableCar(anyString())).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.getAvailableCar(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countCarsTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.countCars()).thenReturn(Mono.just(5L));

        StepVerifier.create(carHandler.countCars(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveCarTest_success() {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(carRequest));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carRequest);

        when(carRequestValidator.handleRequest(any(ServerRequest.class))).thenReturn(serverResponse);
        when(carService.saveCar(any(CarRequest.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.saveCar(serverRequest))
                .expectNextMatches(actualServerResponse -> actualServerResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void uploadCarsTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultiValueMap<String, Part> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("file", List.of(filePart));

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(Mono.just(multiValueMap));

        when(carService.uploadCars(any(FilePart.class))).thenReturn(Flux.just(carResponse));

        StepVerifier.create(carHandler.uploadCars(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarTest_success() {
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(carRequest));

        when(carService.updateCar(anyString(), any(CarRequest.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.updateCar(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_success() {
        CarResponse carDto = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        CarUpdateDetails carUpdateDetails =
                TestUtils.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(carUpdateDetails));

        when(carService.updateCarWhenBookingIsClosed(anyString(), any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(carDto));

        StepVerifier.create(carHandler.updateCarWhenBookingIsClosed(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarStatusTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(CarState.AVAILABLE));

        when(carService.updateCarStatus(anyString(), any(CarState.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.updateCarStatus(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarsStatusTest_success() {
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        UpdateCarRequest updateCarRequest =
                TestUtils.getResourceAsJson("/data/UpdateCarRequest.json", UpdateCarRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .body(Flux.just(updateCarRequest));

        when(carService.updateCarStatus(anyString(), any(CarState.class))).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.updateCarsStatus(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteCarByIdTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.deleteCarById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(carHandler.deleteCarById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
