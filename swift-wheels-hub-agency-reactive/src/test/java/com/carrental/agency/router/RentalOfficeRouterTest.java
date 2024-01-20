package com.carrental.agency.router;

import com.carrental.agency.handler.RentalOfficeHandler;
import com.carrental.agency.util.TestUtils;
import com.carrental.dto.RentalOfficeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = RentalOfficeRouter.class)
@WebFluxTest
class RentalOfficeRouterTest {

    private static final String PATH = "/rental-offices";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private RentalOfficeHandler rentalOfficeHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllRentalOfficesTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        List<RentalOfficeDto> rentalOfficeDtoList = List.of(rentalOfficeDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDtoList);

        when(rentalOfficeHandler.findAllRentalOffices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllRentalOfficesTest_unauthorized() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        List<RentalOfficeDto> rentalOfficeDtoList = List.of(rentalOfficeDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDtoList);

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
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficeByIdTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.findRentalOfficeById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficeByIdTest_unauthorized() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

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
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficeByNameTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.findRentalOfficesByNameInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/office/{name}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficeByNameTest_unauthorized() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.findRentalOfficesByNameInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/office/{name}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
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
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void saveRentalOfficeTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.saveRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveRentalOfficeTest_unauthorized() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.saveRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateRentalOfficeTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.updateRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<RentalOfficeDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(RentalOfficeDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(rentalOfficeDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateRentalOfficeTest_unauthorized() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(rentalOfficeDto);

        when(rentalOfficeHandler.updateRentalOffice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
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
