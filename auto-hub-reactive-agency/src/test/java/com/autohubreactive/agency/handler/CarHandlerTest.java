package com.autohubreactive.agency.handler;

import com.autohubreactive.agency.service.CarService;
import com.autohubreactive.agency.util.TestData;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.dto.common.AvailableCarInfo;
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

    @Test
    void findAllCarsTest_success() {
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.findAllCars()).thenReturn(Flux.fromIterable(carDtoList));

        carHandler.findAllCars(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllCarsTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.findAllCars()).thenReturn(Flux.empty());

        carHandler.findAllCars(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarByIdTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.findCarById(anyString())).thenReturn(Mono.just(carResponse));

        carHandler.findCarById(serverRequest)
                .as(StepVerifier::create)
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

        carHandler.findCarById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarByMakeTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("make", "Volkswagen")
                .build();

        when(carService.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(carDtoList));

        carHandler.findCarsByMakeInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findCarByMakeTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("make", "Volkswagen")
                .build();

        when(carService.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.empty());

        carHandler.findCarsByMakeInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findCarsByFilterTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Volkswagen")
                .build();

        when(carService.findCarsByFilterInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(carDtoList));

        carHandler.findCarsByFilterInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
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

        carHandler.findCarsByFilterInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getAvailableCarTest_success() {
        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.getAvailableCar(anyString())).thenReturn(Mono.just(availableCarInfo));

        carHandler.getAvailableCar(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllAvailableCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.getAllAvailableCars()).thenReturn(Flux.just(carResponse));

        carHandler.getAllAvailableCars(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getCarImageTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.getCarImage(anyString())).thenReturn(Mono.empty());

        carHandler.getCarImage(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void getCarImageTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(carService.getCarImage(anyString())).thenReturn(Mono.just(new byte[]{}));

        carHandler.getCarImage(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countCarsTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.countCars()).thenReturn(Mono.just(5L));

        carHandler.countCars(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveCarTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(multivalueMap));

        when(carService.saveCar(any())).thenReturn(Mono.just(carResponse));

        carHandler.saveCar(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void uploadCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultiValueMap<String, Part> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("file", List.of(filePart));

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .header("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(Mono.just(multiValueMap));

        when(carService.uploadCars(any(FilePart.class))).thenReturn(Flux.just(carResponse));

        carHandler.uploadCars(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MultiValueMap<String, Part> multiValueMap = new LinkedMultiValueMap<>();

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(multiValueMap));

        when(carService.updateCar(anyString(), any())).thenReturn(Mono.just(carResponse));

        carHandler.updateCar(serverRequest)
                .as(StepVerifier::create)
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

        carHandler.deleteCarById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
