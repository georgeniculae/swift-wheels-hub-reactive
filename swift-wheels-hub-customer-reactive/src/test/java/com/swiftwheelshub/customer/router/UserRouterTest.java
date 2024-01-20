package com.swiftwheelshub.customer.router;

import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.customer.handler.CustomerHandler;
import com.swiftwheelshub.customer.util.TestUtils;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = UserRouter.class)
@WebFluxTest
class UserRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerHandler customerHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentUserTest_success() {
        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(currentUserDto);

        when(customerHandler.getCurrentUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<CurrentUserDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/current")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(CurrentUserDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(currentUserDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getCurrentUserTest_unauthorized() {
        CurrentUserDto currentUserDto =
                TestUtils.getResourceAsJson("/data/CurrentUserDto.json", CurrentUserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(currentUserDto);

        when(customerHandler.getCurrentUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/current")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void registerUserTest_success() {
        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(new AuthenticationResponse().token("token"));

        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        when(customerHandler.registerUser(any(ServerRequest.class))).thenReturn(token);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @WithAnonymousUser
    void registerUserTest_forbidden() {
        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(new AuthenticationResponse().token("token"));

        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        when(customerHandler.registerUser(any(ServerRequest.class))).thenReturn(token);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void registerUserTest_unauthorized() {
        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(new AuthenticationResponse().token("token"));

        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        when(customerHandler.registerUser(any(ServerRequest.class))).thenReturn(token);

        webTestClient.post()
                .uri("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void updateUserTest_success() {
        UserDto userDto =
                TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userDto);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_forbidden() {
        UserDto userDto =
                TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userDto);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_unauthorized() {
        UserDto userDto =
                TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userDto);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userDto)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findUserByUsernameTest_success() {
        UserDto userDto =
                TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userDto);

        when(customerHandler.findUserByUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/username/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findUserByUsernameTest_unauthorized() {
        UserDto userDto =
                TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userDto);

        when(customerHandler.findUserByUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countUsersTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(customerHandler.countUsers(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Long> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void countUsersTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(customerHandler.countUsers(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
