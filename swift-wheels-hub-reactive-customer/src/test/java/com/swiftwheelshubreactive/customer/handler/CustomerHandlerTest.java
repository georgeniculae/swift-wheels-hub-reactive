package com.swiftwheelshubreactive.customer.handler;

import com.swiftwheelshubreactive.customer.service.CustomerService;
import com.swiftwheelshubreactive.customer.util.TestUtil;
import com.swiftwheelshubreactive.customer.validator.RegisterRequestValidator;
import com.swiftwheelshubreactive.customer.validator.UserUpdateRequestValidator;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.UserInfo;
import com.swiftwheelshubreactive.dto.UserUpdateRequest;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerHandlerTest {

    @InjectMocks
    private CustomerHandler customerHandler;

    @Mock
    private CustomerService customerService;

    @Mock
    private RegisterRequestValidator registerRequestValidator;

    @Mock
    private UserUpdateRequestValidator userUpdateRequestValidator;

    @Test
    void findAllUsersTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(customerService.findAllUsers()).thenReturn(Flux.just(userInfo));

        StepVerifier.create(customerHandler.findAllUsers(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getCurrentUserTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(customerService.getCurrentUser(anyString())).thenReturn(Mono.just(userInfo));

        StepVerifier.create(customerHandler.getCurrentUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void getCurrentUserTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .header("X-USERNAME", "user")
                .build();

        when(customerService.getCurrentUser(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.getCurrentUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .build();

        when(customerService.findUserByUsername(anyString())).thenReturn(Mono.just(userInfo));

        StepVerifier.create(customerHandler.findUserByUsername(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findUserByUsernameTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .build();

        when(customerService.findUserByUsername(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.findUserByUsername(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countUsersTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("username", "alexandrupopescu")
                .build();

        when(customerService.countUsers()).thenReturn(Mono.just(5L));

        StepVerifier.create(customerHandler.countUsers(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void registerUserTest_success() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(registerRequest));

        when(registerRequestValidator.validateBody(any())).thenReturn(Mono.just(registerRequest));
        when(customerService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.just(registrationResponse));

        StepVerifier.create(customerHandler.registerUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void registerUserTest_noResultReturned() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(registerRequest));

        when(registerRequestValidator.validateBody(any())).thenReturn(Mono.just(registerRequest));
        when(customerService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.registerUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void updateUserTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        UserUpdateRequest userUpdateRequest =
                TestUtil.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f48612b92a3b7dfcebae07")
                .body(Mono.just(userUpdateRequest));

        when(userUpdateRequestValidator.validateBody(any())).thenReturn(Mono.just(userUpdateRequest));
        when(customerService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(Mono.just(userInfo));

        StepVerifier.create(customerHandler.updateUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteCurrentUserTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .header("X-USERNAME", "user")
                .header("X-API-KEY", "apikey")
                .build();

        when(customerService.deleteUserByUsername(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.deleteCurrentUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteUserByUsernameTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .pathVariable("username", "user")
                .header("X-API-KEY", "apikey")
                .build();

        when(customerService.deleteUserByUsername(any(AuthenticationInfo.class), anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.deleteUserByUsername(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void signOutTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f48612b92a3b7dfcebae07")
                .build();

        when(customerService.signOut(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.signOut(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
