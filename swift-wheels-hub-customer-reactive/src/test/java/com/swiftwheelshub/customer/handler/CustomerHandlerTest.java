package com.swiftwheelshub.customer.handler;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
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
    private CustomerService customerService;

    @Test
    void getCurrentUserTest_success() {
        UserInfo userInfo =
                TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

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
                TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

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
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtils.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(registerRequest));

        when(customerService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.just(registrationResponse));

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

        when(customerService.registerUser(any(RegisterRequest.class))).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.registerUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void updateUserTest_success() {
        UserInfo userInfo =
                TestUtils.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        UserUpdateRequest userUpdateRequest =
                TestUtils.getResourceAsJson("/data/UserUpdateRepresentation.json", UserUpdateRequest.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f48612b92a3b7dfcebae07")
                .body(Mono.just(userUpdateRequest));

        when(customerService.updateUser(anyString(), any(UserUpdateRequest.class))).thenReturn(Mono.just(userInfo));

        StepVerifier.create(customerHandler.updateUser(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteUserByIdTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .pathVariable("id", "64f48612b92a3b7dfcebae07")
                .build();

        when(customerService.deleteUserById(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(customerHandler.deleteUserById(serverRequest))
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
