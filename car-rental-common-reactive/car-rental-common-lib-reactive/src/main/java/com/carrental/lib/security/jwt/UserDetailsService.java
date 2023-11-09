package com.carrental.lib.security.jwt;

import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(name = "jwtSecurityConfig")
@Slf4j
public class UserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .switchIfEmpty(
                        Mono.error(
                                () -> new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "User with username " + username + " doesn't exist"
                                )
                        )
                )
                .cast(UserDetails.class)
                .onErrorResume(e -> {
                    log.error("Error while finding by username: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

}
