package com.swiftwheelshub.lib.service;

import com.swiftwheelshub.lib.security.jwt.UserDetailsService;
import com.swiftwheelshub.lib.util.TestUtils;
import com.swiftwheelshub.model.User;
import com.swiftwheelshub.lib.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceTest {

    @InjectMocks
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void findByUsernameTest_success() {
        User user = TestUtils.getResourceAsJson("/data/UserDto.json", User.class);

        when(userRepository.findByUsername(anyString())).thenReturn(Mono.just(user));

        StepVerifier.create(userDetailsService.findByUsername("alexandrupopescu"))
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void findByUsernameTest_emptyResult() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(userDetailsService.findByUsername("alexandrupopescu"))
                .expectError()
                .verify();
    }

    @Test
    void findByUsernameTest_errorOnFindingByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(userDetailsService.findByUsername("alexandrupopescu"))
                .expectError()
                .verify();
    }

}
