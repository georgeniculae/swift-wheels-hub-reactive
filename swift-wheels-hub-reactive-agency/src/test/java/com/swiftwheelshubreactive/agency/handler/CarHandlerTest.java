package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.CarService;
import com.swiftwheelshubreactive.agency.util.TestData;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.agency.validator.CarUpdateDetailsValidator;
import com.swiftwheelshubreactive.agency.validator.UpdateCarRequestValidator;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
    private CarUpdateDetailsValidator carUpdateDetailsValidator;

    @Mock
    private UpdateCarRequestValidator updateCarRequestValidator;

    @Test
    void findAllCarsTest_success() {
        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("make", "Volkswagen")
                .build();

        when(carService.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(carDtoList));

        StepVerifier.create(carHandler.findCarsByMakeInsensitiveCase(serverRequest))
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

        StepVerifier.create(carHandler.findCarsByMakeInsensitiveCase(serverRequest))
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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

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
    void findAllAvailableCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(carService.getAllAvailableCars()).thenReturn(Flux.just(carResponse));

        StepVerifier.create(carHandler.getAllAvailableCars(serverRequest))
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

        StepVerifier.create(carHandler.getCarImage(serverRequest))
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

        StepVerifier.create(carHandler.getCarImage(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countCarsTest_success() {
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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(multivalueMap));

        when(carService.saveCar(any())).thenReturn(Mono.just(carResponse));

        StepVerifier.create(carHandler.saveCar(serverRequest))
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

        StepVerifier.create(carHandler.uploadCars(serverRequest))
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

        StepVerifier.create(carHandler.updateCar(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_success() {
        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(carUpdateDetails));

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/StatusUpdateResponse.json", StatusUpdateResponse.class);

        when(carUpdateDetailsValidator.validateBody(any())).thenReturn(Mono.just(carUpdateDetails));
        when(carService.updateCarWhenBookingIsClosed(anyString(), any(CarUpdateDetails.class)))
                .thenReturn(Mono.just(statusUpdateResponse));

        StepVerifier.create(carHandler.updateCarWhenBookingIsClosed(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarStatusTest_success() {
        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/StatusUpdateResponse.json", StatusUpdateResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PATCH)
                .pathVariable("id", "64f361caf291ae086e179547")
                .queryParam("carState", "AVAILABLE")
                .body(Mono.just(CarState.AVAILABLE));

        when(carService.updateCarStatus(anyString(), any(CarState.class))).thenReturn(Mono.just(statusUpdateResponse));

        StepVerifier.create(carHandler.updateCarStatus(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateCarsStatusesTest_success() {
        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/StatusUpdateResponse.json", StatusUpdateResponse.class);

        UpdateCarRequest updateCarRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarRequest.json", UpdateCarRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .body(Flux.just(updateCarRequest));

        when(updateCarRequestValidator.validateBody(any())).thenReturn(Mono.just(updateCarRequest));
        when(carService.updateCarsStatus(anyList())).thenReturn(Flux.just(statusUpdateResponse));

        StepVerifier.create(carHandler.updateCarsStatuses(serverRequest))
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
