package com.carrental.agency.router;

import com.carrental.agency.handler.CarHandler;
import com.carrental.agency.util.TestUtils;
import com.carrental.dto.CarDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = CarRouter.class)
@WebFluxTest
class CarRouterTest {

    private static final String PATH = "/cars";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CarHandler carHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllCarsTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        List<CarDto> carDtoList = List.of(carDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDtoList);

        when(carHandler.findAllCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllCarsTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        List<CarDto> carDtoList = List.of(carDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDtoList);

        when(carHandler.findAllCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findCarByIdTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.findCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findCarByIdTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.findCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findCarsByMakeTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.findCarsByMake(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/make/{make}", "Volkswagen")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findCarsByMakeTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.findCarsByMake(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/make/{make}", "Volkswagen")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countCarsTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(carHandler.countCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Long> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void countCarsTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(carHandler.countCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void saveCarTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.saveCar(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveCarTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.saveCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void saveCarTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.saveCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void uploadCarsTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carDto));

        when(carHandler.uploadCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH + "/upload")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void uploadCarsTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carDto));

        when(carHandler.uploadCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH + "/upload")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void uploadCarsTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carDto));

        when(carHandler.uploadCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH + "/upload")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateCarTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCar(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateCarStatusTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/change-car-status", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateCarStatusTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/update-car-status", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void updateCarStatusTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}/update-car-status", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateCarsStatusTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarsStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/update-cars-status")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateCarsStatusTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarsStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/update-cars-status")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void updateCarsStatusTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarsStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/update-cars-status")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void setCarStatusNotAvailableTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/change-car-status", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void setCarStatusNotAvailableTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/set-car-not-available", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void setCarStatusNotAvailableTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarStatus(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}/set-car-not-available", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateCarStatusAfterClosedBookingTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarWhenBookingIsClosed(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/update-after-closed-booking", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateCarStatusAfterClosedBookingTest_unauthorized() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarWhenBookingIsClosed(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}/update-after-closed-booking", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void updateCarStatusAfterClosedBookingTest_forbidden() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDto);

        when(carHandler.updateCarWhenBookingIsClosed(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}/update-after-closed-booking", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void deleteCarByIdTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(carHandler.deleteCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Void> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent()
                .returnResult(Void.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectComplete()
                .verify();
    }

    @Test
    @WithAnonymousUser
    void deleteCarByIdTest_forbidden() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(carHandler.deleteCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
