package com.swiftwheelshubreactive.customer.handler;

import com.swiftwheelshubreactive.customer.service.CustomerService;
import com.swiftwheelshubreactive.customer.validator.RegisterRequestValidator;
import com.swiftwheelshubreactive.customer.validator.UserUpdateRequestValidator;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CustomerHandler {

    private static final String USERNAME = "username";
    private static final String ID = "id";
    private final CustomerService customerService;
    private final RegisterRequestValidator registerRequestValidator;
    private final UserUpdateRequestValidator userUpdateRequestValidator;

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findAllUsers(ServerRequest serverRequest) {
        return customerService.findAllUsers()
                .collectList()
                .flatMap(userInfos -> ServerResponse.ok().bodyValue(userInfos));
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> getCurrentUser(ServerRequest serverRequest) {
        return customerService.getCurrentUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(userInfo -> ServerResponse.ok().bodyValue(userInfo))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findUserByUsername(ServerRequest serverRequest) {
        return customerService.findUserByUsername(ServerRequestUtil.getPathVariable(serverRequest, USERNAME))
                .flatMap(userInfo -> ServerResponse.ok().bodyValue(userInfo))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> countUsers(ServerRequest serverRequest) {
        return customerService.countUsers()
                .flatMap(userCount -> ServerResponse.ok().bodyValue(userCount));
    }

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterRequest.class)
                .flatMap(registerRequestValidator::validateBody)
                .flatMap(customerService::registerUser)
                .flatMap(authenticationResponse -> ServerResponse.ok().bodyValue(authenticationResponse))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserUpdateRequest.class)
                .flatMap(userUpdateRequestValidator::validateBody)
                .flatMap(updatedUser -> customerService.updateUser(
                                ServerRequestUtil.getPathVariable(serverRequest, ID),
                                updatedUser
                        )
                )
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> deleteUserByUsername(ServerRequest serverRequest) {
        return customerService.deleteUserByUsername(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getRolesHeader(serverRequest),
                        ServerRequestUtil.getPathVariable(serverRequest, USERNAME)
                )
                .then(ServerResponse.noContent().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> deleteCurrentUser(ServerRequest serverRequest) {
        return customerService.deleteUserByUsername(
                        ServerRequestUtil.getApiKeyHeader(serverRequest),
                        ServerRequestUtil.getRolesHeader(serverRequest),
                        ServerRequestUtil.getUsername(serverRequest)
                )
                .then(ServerResponse.noContent().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> signOut(ServerRequest serverRequest) {
        return customerService.signOut(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.ok().build());
    }

}
