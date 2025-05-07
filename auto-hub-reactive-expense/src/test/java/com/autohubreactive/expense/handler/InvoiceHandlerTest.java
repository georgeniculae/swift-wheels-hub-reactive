package com.autohubreactive.expense.handler;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoice.InvoiceRequest;
import com.autohubreactive.expense.service.InvoiceService;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.expense.validator.InvoiceRequestValidator;
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
class InvoiceHandlerTest {

    @InjectMocks
    private InvoiceHandler invoiceHandler;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private InvoiceRequestValidator invoiceRequestValidator;

    @Test
    void findAllInvoicesTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllInvoices()).thenReturn(Flux.just(invoiceResponse));

        invoiceHandler.findAllInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllInvoicesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllInvoices()).thenReturn(Flux.empty());

        invoiceHandler.findAllInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findAllActiveInvoices_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllActiveInvoices()).thenReturn(Flux.just(invoiceResponse));

        invoiceHandler.findAllActiveInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllActiveInvoices_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllActiveInvoices()).thenReturn(Flux.empty());

        invoiceHandler.findAllActiveInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findAllInvoicesByCustomerUsername_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("customerId", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findAllInvoicesByCustomerUsername(anyString())).thenReturn(Flux.just(invoiceResponse));

        invoiceHandler.findAllInvoicesByCustomerUsername(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllInvoicesByCustomerUsername_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("customerId", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findAllInvoicesByCustomerUsername(anyString())).thenReturn(Flux.empty());

        invoiceHandler.findAllInvoicesByCustomerUsername(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findInvoiceById_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findInvoiceById(anyString())).thenReturn(Mono.just(invoiceResponse));

        invoiceHandler.findInvoiceById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findInvoiceById_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findInvoiceById(anyString())).thenReturn(Mono.empty());

        invoiceHandler.findInvoiceById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findInvoiceByComments_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("comments", "comment")
                .build();

        when(invoiceService.findInvoicesByComments(anyString())).thenReturn(Flux.just(invoiceResponse));

        invoiceHandler.findInvoicesByComments(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findInvoiceByComments_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("comments", "comment")
                .build();

        when(invoiceService.findInvoicesByComments(anyString())).thenReturn(Flux.empty());

        invoiceHandler.findInvoicesByComments(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countInvoices_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(1));

        when(invoiceService.countInvoices()).thenReturn(Mono.just(1L));

        invoiceHandler.countInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countAllActiveInvoices_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(1));

        when(invoiceService.countAllActiveInvoices()).thenReturn(Mono.just(1L));

        invoiceHandler.countAllActiveInvoices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateInvoiceTest_success() {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .header("X-API-KEY", "apikey")
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(invoiceRequest));

        when(invoiceRequestValidator.validateBody(any())).thenReturn(Mono.just(invoiceRequest));
        when(invoiceService.closeInvoice(anyString(), any(InvoiceRequest.class)))
                .thenReturn(Mono.just(invoiceResponse));

        invoiceHandler.closeInvoice(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
