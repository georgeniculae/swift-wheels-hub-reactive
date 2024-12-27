package com.swiftwheelshubreactive.expense.handler;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.service.InvoiceService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.expense.validator.InvoiceRequestValidator;
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

        StepVerifier.create(invoiceHandler.findAllInvoices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllInvoicesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllInvoices()).thenReturn(Flux.empty());

        StepVerifier.create(invoiceHandler.findAllInvoices(serverRequest))
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

        StepVerifier.create(invoiceHandler.findAllActiveInvoices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllActiveInvoices_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllActiveInvoices()).thenReturn(Flux.empty());

        StepVerifier.create(invoiceHandler.findAllActiveInvoices(serverRequest))
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

        StepVerifier.create(invoiceHandler.findAllInvoicesByCustomerUsername(serverRequest))
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

        StepVerifier.create(invoiceHandler.findAllInvoicesByCustomerUsername(serverRequest))
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

        StepVerifier.create(invoiceHandler.findInvoiceById(serverRequest))
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

        StepVerifier.create(invoiceHandler.findInvoiceById(serverRequest))
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

        StepVerifier.create(invoiceHandler.findInvoicesByComments(serverRequest))
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

        StepVerifier.create(invoiceHandler.findInvoicesByComments(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countInvoices_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(1));

        when(invoiceService.countInvoices()).thenReturn(Mono.just(1L));

        StepVerifier.create(invoiceHandler.countInvoices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void countAllActiveInvoices_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .body(Mono.just(1));

        when(invoiceService.countAllActiveInvoices()).thenReturn(Mono.just(1L));

        StepVerifier.create(invoiceHandler.countAllActiveInvoices(serverRequest))
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

        StepVerifier.create(invoiceHandler.closeInvoice(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
