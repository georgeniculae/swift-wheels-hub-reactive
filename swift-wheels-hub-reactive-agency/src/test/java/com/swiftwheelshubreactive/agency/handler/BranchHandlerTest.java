package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.BranchService;
import com.swiftwheelshubreactive.agency.util.TestUtils;
import com.swiftwheelshubreactive.agency.validator.BranchRequestValidator;
import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.dto.BranchResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchHandlerTest {

    @InjectMocks
    private BranchHandler branchHandler;

    @Mock
    private BranchService branchService;

    @Mock
    private BranchRequestValidator branchRequestValidator;

    @Test
    void findAllBranchesTest_success() {
        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        List<BranchResponse> branchResponses = List.of(branchResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(branchService.findAllBranches()).thenReturn(Flux.fromIterable(branchResponses));

        StepVerifier.create(branchHandler.findAllBranches(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllBranchesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(branchService.findAllBranches()).thenReturn(Flux.empty());

        StepVerifier.create(branchHandler.findAllBranches(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findBranchByIdTest_success() {
        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(branchService.findBranchById(anyString())).thenReturn(Mono.just(branchResponse));

        StepVerifier.create(branchHandler.findBranchById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findBranchByIdTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(branchService.findBranchById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(branchHandler.findBranchById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findBranchByFilterTest_success() {
        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "filter")
                .build();

        when(branchService.findBranchesByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(branchResponse));

        StepVerifier.create(branchHandler.findBranchByFilterInsensitiveCase(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countBranchesTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(branchService.countBranches()).thenReturn(Mono.just(5L));

        StepVerifier.create(branchHandler.countBranches(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveBranchTest_success() {
        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(branchRequest));

        when(branchRequestValidator.validateBody(any())).thenReturn(Mono.just(branchRequest));
        when(branchService.saveBranch(any(BranchRequest.class))).thenReturn(Mono.just(branchResponse));

        StepVerifier.create(branchHandler.saveBranch(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateBranchTest_success() {
        BranchRequest branchRequest =
                TestUtils.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);

        BranchResponse branchResponse =
                TestUtils.getResourceAsJson("/data/BranchResponse.json", BranchResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(branchRequest));

        when(branchRequestValidator.validateBody(any())).thenReturn(Mono.just(branchRequest));
        when(branchService.updateBranch(anyString(), any(BranchRequest.class))).thenReturn(Mono.just(branchResponse));

        StepVerifier.create(branchHandler.updateBranch(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteBranchByIdTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(branchService.deleteBranchById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(branchHandler.deleteBranchById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
