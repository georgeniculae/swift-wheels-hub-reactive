package com.swiftwheelshub.customer.handler;

import com.swiftwheelshub.customer.service.CustomerService;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import com.swiftwheelshub.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
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

    public Mono<ServerResponse> getCurrentUser(ServerRequest serverRequest) {
        return customerService.getCurrentUser(ServerRequestUtil.getUsername(serverRequest))
                .flatMap(currentUserDto -> ServerResponse.ok().bodyValue(currentUserDto))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> findUserByUsername(ServerRequest serverRequest) {
        return customerService.findUserByUsername(ServerRequestUtil.getPathVariable(serverRequest, USERNAME))
                .flatMap(userDto -> ServerResponse.ok().bodyValue(userDto))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> countUsers(ServerRequest serverRequest) {
        return customerService.countUsers()
                .flatMap(userCount -> ServerResponse.ok().bodyValue(userCount));
    }

    public Mono<ServerResponse> registerUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterRequest.class)
                .flatMap(customerService::registerUser)
                .flatMap(authenticationResponse -> ServerResponse.ok().bodyValue(authenticationResponse))
                .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserDto.class)
                .flatMap(userDto -> customerService.updateUser(ServerRequestUtil.getPathVariable(serverRequest, ID), userDto))
                .flatMap(user -> ServerResponse.ok().bodyValue(user));
    }

    public Mono<ServerResponse> deleteUserById(ServerRequest serverRequest) {
        return customerService.deleteUserById(ServerRequestUtil.getPathVariable(serverRequest, USERNAME))
                .then(ServerResponse.noContent().build());
    }

}
