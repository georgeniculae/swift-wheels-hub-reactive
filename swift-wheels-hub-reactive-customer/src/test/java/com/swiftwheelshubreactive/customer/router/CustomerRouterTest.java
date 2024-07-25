package com.swiftwheelshubreactive.customer.router;

import com.swiftwheelshubreactive.customer.handler.CustomerHandler;
import com.swiftwheelshubreactive.customer.util.TestUtil;
import com.swiftwheelshubreactive.dto.RegisterRequest;
import com.swiftwheelshubreactive.dto.RegistrationResponse;
import com.swiftwheelshubreactive.dto.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = CustomerRouter.class)
class CustomerRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerHandler customerHandler;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllUsersTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(userInfo));

        when(customerHandler.findAllUsers(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserInfo> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/infos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserInfo.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllUsersTest_unauthorized() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(userInfo));

        when(customerHandler.findAllUsers(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/infos")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentUserTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

        when(customerHandler.getCurrentUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserInfo> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/current")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserInfo.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void getCurrentUserTest_unauthorized() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void registerUserTest_success() {
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(registrationResponse);

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
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(registrationResponse);
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
        RegisterRequest registerRequest =
                TestUtil.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        RegistrationResponse registrationResponse =
                TestUtil.getResourceAsJson("/data/RegistrationResponse.json", RegistrationResponse.class);

        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(registrationResponse);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateUserTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserInfo> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userInfo)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserInfo.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_forbidden() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userInfo)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    @WithAnonymousUser
    void updateUserTest_unauthorized() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

        when(customerHandler.updateUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri("/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userInfo)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findUserByUsernameTest_success() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

        when(customerHandler.findUserByUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<UserInfo> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri("/username/{username}", "admin")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(UserInfo.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(userInfo)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findUserByUsernameTest_unauthorized() {
        UserInfo userInfo =
                TestUtil.getResourceAsJson("/data/UserInfo.json", UserInfo.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(userInfo);

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
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
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

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteCurrentUserTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().build();

        when(customerHandler.deleteCurrentUser(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Void> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/current")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectComplete()
                .verify();
    }

    @Test
    @WithAnonymousUser
    void deleteCurrentUserTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().build();

        when(customerHandler.deleteCurrentUser(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/current")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteUserByUsernameTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().build();

        when(customerHandler.deleteUserByUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Void> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/{username}", "user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Void.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectComplete()
                .verify();
    }

    @Test
    @WithAnonymousUser
    void deleteUserByUsernameTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().build();

        when(customerHandler.deleteUserByUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .delete()
                .uri("/{username}", "user")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

}
