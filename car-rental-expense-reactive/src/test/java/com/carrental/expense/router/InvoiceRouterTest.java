package com.carrental.expense.router;

import com.carrental.dto.InvoiceDto;
import com.carrental.expense.handler.InvoiceHandler;
import com.carrental.expense.util.TestUtils;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InvoiceRouter.class)
@WebFluxTest
public class InvoiceRouterTest {

    private static final String PATH = "/invoices";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InvoiceHandler invoiceHandler;

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllInvoicesTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllInvoicesTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllActiveInvoicesTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllActiveInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllActiveInvoicesTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllActiveInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findAllInvoicesByCustomerIdTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllInvoicesByCustomerUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/by-customer/{customerUsername}", "username")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findAllInvoicesByCustomerIdTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findAllInvoicesByCustomerUsername(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/by-customer/{customerUsername}", "username")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countInvoicesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(invoiceHandler.countInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Long> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
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
    void countInvoicesTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(invoiceHandler.countInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void countAllActiveInvoicesTest_success() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(invoiceHandler.countAllActiveInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<Long> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count-active")
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
    void countAllActiveInvoicesTest_unauthorized() {
        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(1);

        when(invoiceHandler.countAllActiveInvoices(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/count-active")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findInvoiceByIdTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findInvoiceById(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findInvoiceByIdTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findInvoiceById(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void findInvoiceByCommentsTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findInvoiceByComments(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/by-comments", "comment")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void findInvoiceByCommentsTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.findInvoiceByComments(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .get()
                .uri(PATH + "/by-comments", "comment")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithMockUser(value = "admin", username = "admin", password = "admin", roles = "ADMIN")
    void closeInvoiceTest_success() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.closeInvoice(any(ServerRequest.class))).thenReturn(serverResponse);

        Flux<InvoiceDto> responseBody = webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(InvoiceDto.class)
                .getResponseBody();

        StepVerifier.create(responseBody)
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    @WithAnonymousUser
    void closeInvoiceTest_unauthorized() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.closeInvoice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.mutateWith(SecurityMockServerConfigurers.csrf())
                .put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    @WithAnonymousUser
    void closeInvoiceTest_forbidden() {
        InvoiceDto invoiceDto =
                TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        Mono<ServerResponse> serverResponse = ServerResponse.ok().bodyValue(List.of(invoiceDto));

        when(invoiceHandler.closeInvoice(any(ServerRequest.class))).thenReturn(serverResponse);

        webTestClient.put()
                .uri(PATH + "/{id}", "64f361caf291ae086e179547")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

}
