package com.swiftwheelshub.customer.handler;

import com.swiftwheelshub.dto.AuthenticationResponse;
import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.customer.service.KeycloakUserService;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerHandlerTest {

    @InjectMocks
    private CustomerHandler customerHandler;

    @Mock
    private KeycloakUserService keycloakUserService;

    @Test
    void getCurrentUserTest_success() {
        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .body(Mono.just(currentUserDto));

        when(keycloakUserService.getCurrentUser(anyString())).thenReturn(Mono.just(currentUserDto));

        StepVerifier.create(customerHandler.getCurrentUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getCurrentUserTest_noResultReturned() {
        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .body(Mono.just(currentUserDto));

        when(keycloakUserService.getCurrentUser(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.getCurrentUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .body(Mono.just(userDto));

        when(keycloakUserService.findUserByUsername(anyString())).thenReturn(Mono.just(userDto));

        StepVerifier.create(customerHandler.findUserByUsername(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_noResultReturned() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .body(Mono.just(userDto));

        when(keycloakUserService.findUserByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.findUserByUsername(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countUsersTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .body(Mono.just(5));

        when(keycloakUserService.countUsers()).thenReturn(Mono.just(5L));

        StepVerifier.create(customerHandler.countUsers(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void registerUserTest_success() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        String token = "token";
        AuthenticationResponse authenticationResponse = new AuthenticationResponse().token(token);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(registerRequest));

        when(keycloakUserService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.just(authenticationResponse));

        StepVerifier.create(customerHandler.registerUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void registerUserTest_noResultReturned() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(registerRequest));

        when(keycloakUserService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.registerUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void updateUserTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f48612b92a3b7dfcebae07")
                .body(Mono.just(userDto));

        when(keycloakUserService.updateUser(anyString(), any(UserDto.class))).thenReturn(Mono.just(userDto));

        StepVerifier.create(customerHandler.updateUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
