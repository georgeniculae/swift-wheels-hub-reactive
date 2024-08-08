package com.swiftwheelshubreactive.agency.router;

import com.swiftwheelshubreactive.agency.handler.BranchHandler;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.BranchResponse;
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
@ContextConfiguration(classes = BranchRouter.class)
class BranchRouterTest {

    private static final String PATH = "/branches";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BranchHandler branchHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllBranchesTest_success() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        List<BranchResponse> branchResponses = List.of(branchResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponses);

        when(branchHandler.findAllBranches(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllBranchesTest_unauthorized() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        List<BranchResponse> branchResponses = List.of(branchResponse);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponses);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByIdTest_success() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.findBranchById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser()
    void findBranchByIdTest_unauthorized() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByFilterTest_success() {
        BranchResponse branchResponse = TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.findBranchesByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/filter/{filter}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBranchByFilterTest_unauthorized() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.findBranchesByFilterInsensitiveCase(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/filter/{filter}", "Test")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countBranchesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(5L);

        when(branchHandler.countBranches(any(ServerRequest.class))).thenReturn(serverResponse);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void saveBranchTest_success() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.saveBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(branchResponse)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void saveBranchTest_unauthorized() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

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
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.saveBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.post()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateBranchTest_success() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

        when(branchHandler.updateBranch(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(branchResponse)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateBranchTest_forbidden() {
        BranchResponse branchResponse =
                TestUtil.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(branchResponse);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteBranchByIdTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.noContent().build();

        when(branchHandler.deleteBranchById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<BranchResponse> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri(PATH + "/{id}", "64f48612b92a3b7dfcebae07")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNoContent().returnResult(BranchResponse.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectComplete()
                .verify();
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
