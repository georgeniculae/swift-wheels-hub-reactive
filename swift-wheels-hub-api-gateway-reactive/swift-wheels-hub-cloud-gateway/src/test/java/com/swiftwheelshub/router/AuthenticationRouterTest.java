package com.swiftwheelshub.router;

import com.swiftwheelshub.cloudgateway.handler.AuthenticationHandler;
import com.swiftwheelshub.cloudgateway.router.AuthenticationRouter;
import com.swiftwheelshub.dto.AuthenticationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AuthenticationRouter.class})
@WebFluxTest
class AuthenticationRouterTest {

    private static final String PATH = "/authenticate";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuthenticationHandler authenticationHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void authenticateRouteTest() {
        Mono<ServerResponse> token = ServerResponse.ok().bodyValue(new AuthenticationResponse().token("token"));
        when(authenticationHandler.authenticateUser(any())).thenReturn(token);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();
    }

}
