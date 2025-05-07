package com.autohubreactive.agency.router;

import com.autohubreactive.agency.handler.CarHandler;
import com.autohubreactive.agency.util.TestData;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.agency.CarResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

@WebFluxTest
@ContextConfiguration(classes = CarRouter.class)
class CarRouterTest {

    private static final String PATH = "/cars";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CarHandler carHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponses);

        when(carHandler.findAllCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllCarsTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carDtoList = List.of(carResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carDtoList);

        when(carHandler.findAllCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarByIdTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.findCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findCarByIdTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.findCarById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAvailableCarTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.getAvailableCar(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.get()
                .uri(PATH + "/{id}" + "/availability", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk().returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAvailableCarTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.getAvailableCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/{id}" + "/availability", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getAllAvailableCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponses);

        when(carHandler.getAllAvailableCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.get()
                .uri(PATH + "/available")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getAllAvailableCarsTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        List<CarResponse> carResponses = List.of(carResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponses);

        when(carHandler.getAllAvailableCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/available")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findCarsByMakeTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.findCarsByMakeInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.get()
                .uri(PATH + "/make/{make}", "Volkswagen")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findCarsByMakeTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.findCarsByMakeInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/make/{make}", "Volkswagen")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getCarImageTest_success() {
        byte[] body = new byte[]{};
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(body);

        when(carHandler.getCarImage(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<byte[]> responseBody = webTestClient.get()
                .uri(PATH + "/{id}/image", "64f361caf291ae086e179547")
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(byte[].class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNextMatches(bytes -> bytes.length == body.length)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getCarImageTest_unauthorized() {
        byte[] body = new byte[]{};
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(body);

        when(carHandler.getCarImage(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/{id}/image", "64f361caf291ae086e179547")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countCarsTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(carHandler.countCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Long> responseBody = webTestClient.get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(5L)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void countCarsTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(carHandler.countCars(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void saveCarTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.saveCar(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(TestData.getCarRequestMultivalueMap()))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveCarTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.saveCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void uploadCarsTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carResponse));

        when(carHandler.uploadCars(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH + "/upload")
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void uploadCarsTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carResponse));

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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ClassPathResource("src/test/resources/file/Cars.xlsx"));

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(carResponse));

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateCarTest_success() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.updateCar(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CarResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .body(BodyInserters.fromMultipartData(TestData.getCarRequestMultivalueMap()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CarResponse.class)
                .getResponseBody();

        responseBody.as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateCarTest_unauthorized() {
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

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
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(carResponse);

        when(carHandler.updateCar(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
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

        responseBody.as(StepVerifier::create)
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
