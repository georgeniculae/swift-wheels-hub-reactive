package com.carrental.agency.router;

import com.carrental.agency.handler.BranchHandler;
import com.carrental.agency.util.TestUtils;
import com.carrental.dto.BranchDto;
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
@ContextConfiguration(classes = BranchRouter.class)
@WebFluxTest
class BranchRouterTest {

    private static final String PATH = "/branches";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BranchHandler branchHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllBranchesTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        List<BranchDto> branchDtoList = List.of(branchDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDtoList);

        when(branchHandler.findAllBranches(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(BranchDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllBranchesTest_unauthorized() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);
        List<BranchDto> branchDtoList = List.of(branchDto);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDtoList);

        when(branchHandler.findAllBranches(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void findBranchByIdTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.findBranchById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();
    }

    @Test
    @WithAnonymousUser()
    void findBranchByIdTest_unauthorized() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.findBranchById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByFilterTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.findBranchByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/filter/{filter}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByFilterTest_unauthorized() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.findBranchByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/filter/{filter}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countBranchesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(branchHandler.countBranches(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Long.class);
    }

    @Test
    @WithAnonymousUser
    void countBranchesTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5);

        when(branchHandler.countBranches(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void saveBranchTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.saveBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(branchDto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BranchDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveBranchTest_unauthorized() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.saveBranch(any(ServerRequest.class))).thenReturn(serverResponse);

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
    void saveBranchTest_forbidden() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.saveBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateBranchTest_success() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.updateBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(BranchDto.class);
    }

    @Test
    @WithAnonymousUser
    void updateBranchTest_forbidden() {
        BranchDto branchDto = TestUtils.getResourceAsJson("/data/BranchDto.json", BranchDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchDto);

        when(branchHandler.updateBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void deleteBranchByIdTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(branchHandler.deleteBranchById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @WithAnonymousUser
    void deleteBranchByIdTest_forbidden() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(branchHandler.updateBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
