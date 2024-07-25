package com.swiftwheelshubreactive.agency.router;

import com.swiftwheelshubreactive.agency.handler.RentalOfficeHandler;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.RentalOfficeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = RentalOfficeRouter.class)
class RentalOfficeRouterTest {

    private static final String PATH = "/rental-offices";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RentalOfficeHandler rentalOfficeHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllRentalOfficesTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        List<RentalOfficeResponse> rentalOfficeResponses = List.of(rentalOfficeResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponses);

        when(rentalOfficeHandler.findAllRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllRentalOfficesTest_unauthorized() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        List<RentalOfficeResponse> rentalOfficeResponses = List.of(rentalOfficeResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponses);

        when(rentalOfficeHandler.findAllRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficeByIdTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.findRentalOfficeById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficeByIdTest_unauthorized() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.findAllRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficeByFilterTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.findRentalOfficesByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/filter/{filter}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficeByFilterTest_unauthorized() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.findRentalOfficesByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/office/{name}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countRentalOfficesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(rentalOfficeHandler.countRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void countRentalOfficesTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(rentalOfficeHandler.countRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void saveRentalOfficeTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.saveRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveRentalOfficeTest_unauthorized() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.saveRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void saveRentalOfficeTest_forbidden() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.saveRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateRentalOfficeTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.updateRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateRentalOfficeTest_unauthorized() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.updateRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void updateRentalOfficeTest_forbidden() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeResponse);

        when(rentalOfficeHandler.updateRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteRentalOfficeByIdTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(rentalOfficeHandler.deleteRentalOfficeById(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void deleteRentalOfficeByIdTest_forbidden() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(rentalOfficeHandler.deleteRentalOfficeById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
