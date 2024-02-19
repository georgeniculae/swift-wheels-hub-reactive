package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
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
        return Mono.fromCallable(() -> keycloakUserService.getCurrentUser(username))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserInfo> getCurrentUser(String username) {
        return Mono.fromCallable(() -> keycloakUserService.getCurrentUser(username))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Long> countUsers() {
        return Mono.fromCallable(keycloakUserService::countUsers)
                .subscribeOn(Schedulers.boundedElastic())
                .cast(Long.class);
    }

    public Mono<RegistrationResponse> registerUser(RegisterRequest request) {
        return Mono.fromCallable(() -> keycloakUserService.registerCustomer(request))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<UserInfo> updateUser(String id, UserUpdateRequest userUpdateRequest) {
        return Mono.fromCallable(() -> keycloakUserService.updateUser(id, userUpdateRequest))
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Void> deleteUserByUsername(String username) {
        return Mono.fromRunnable(() -> keycloakUserService.deleteUserByUsername(username))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    public Mono<Void> signOut(String username) {
        return Mono.fromRunnable(() -> keycloakUserService.signOut(username))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

}
