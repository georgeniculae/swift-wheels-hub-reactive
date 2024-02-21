package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final KeycloakUserService keycloakUserService;

    public Mono<UserInfo> findUserByUsername(String username) {
        return Mono.fromCallable(() -> keycloakUserService.findUserByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while getting user by username: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<UserInfo> getCurrentUser(String username) {
        return Mono.fromCallable(() -> keycloakUserService.getCurrentUser(username))
                .subscribeOn(Schedulers.boundedElastic()).onErrorMap(e -> {
                    log.error("Error while getting current user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<Long> countUsers() {
        return Mono.fromCallable(keycloakUserService::countUsers)
                .subscribeOn(Schedulers.boundedElastic())
                .map(Long::valueOf)
                .onErrorMap(e -> {
                    log.error("Error while counting users: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<RegistrationResponse> registerUser(RegisterRequest request) {
        return Mono.fromCallable(() -> keycloakUserService.registerCustomer(request))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while registering user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<UserInfo> updateUser(String id, UserUpdateRequest userUpdateRequest) {
        return Mono.fromCallable(() -> keycloakUserService.updateUser(id, userUpdateRequest))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while updating user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                });
    }

    public Mono<Void> deleteUserById(String id) {
        return Mono.fromRunnable(() -> keycloakUserService.deleteUserById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while deleting user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                })
                .then();
    }

    public Mono<Void> signOut(String id) {
        return Mono.fromRunnable(() -> keycloakUserService.signOut(id))
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(e -> {
                    log.error("Error while signing out user: {}", e.getMessage());

                    return new SwiftWheelsHubException(e);
                })
                .then();
    }

}
