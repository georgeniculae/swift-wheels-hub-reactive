package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.BranchService;
import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private static final String ID = "id";
    private static final String FILTER = "filter";
    private final BranchService branchService;

    public Mono<ServerResponse> findAllBranches(ServerRequest serverRequest) {
        return branchService.findAllBranches()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(branchResponses -> ServerResponse.ok().bodyValue(branchResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBranchById(ServerRequest serverRequest) {
        return branchService.findBranchById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(branchResponse -> ServerResponse.ok().bodyValue(branchResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findBranchByFilterInsensitiveCase(ServerRequest serverRequest) {
        return branchService.findBranchesByFilterInsensitiveCase(ServerRequestUtil.getPathVariable(serverRequest, FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(branchResponses -> ServerResponse.ok().bodyValue(branchResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> updateBranch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequest.class)
                .flatMap(branchRequest -> branchService.updateBranch(ServerRequestUtil.getPathVariable(serverRequest, ID), branchRequest))
                .flatMap(branchResponse -> ServerResponse.ok().bodyValue(branchResponse));
    }

    public Mono<ServerResponse> countBranches(ServerRequest serverRequest) {
        return branchService.countBranches()
                .flatMap(numberOfBranches -> ServerResponse.ok().bodyValue(numberOfBranches));
    }

    public Mono<ServerResponse> saveBranch(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(BranchRequest.class)
                .flatMap(branchService::saveBranch)
                .flatMap(branchResponse -> ServerResponse.ok().bodyValue(branchResponse));
    }

    public Mono<ServerResponse> deleteBranchById(ServerRequest serverRequest) {
        return branchService.deleteBranchById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.noContent().build());
    }

}
