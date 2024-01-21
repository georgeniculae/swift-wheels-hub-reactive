package com.swiftwheelshub.expense.handler;

import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.expense.util.TestUtils;
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

    @Test
    void findAllInvoicesTest_success() {
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllInvoices()).thenReturn(Flux.just(invoiceDto));

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
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(invoiceService.findAllActiveInvoices()).thenReturn(Flux.just(invoiceDto));

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
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("customerId", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findAllInvoicesByCustomerUsername(anyString())).thenReturn(Flux.just(invoiceDto));

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
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(invoiceService.findInvoiceById(anyString())).thenReturn(Mono.just(invoiceDto));

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
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("comments", "comment")
                .build();

        when(invoiceService.findInvoicesByComments(anyString())).thenReturn(Flux.just(invoiceDto));

        StepVerifier.create(invoiceHandler.findInvoiceByComments(serverRequest))
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

        StepVerifier.create(invoiceHandler.findInvoiceByComments(serverRequest))
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
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .header("X-API-KEY", "apikey")
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(invoiceDto));

        when(invoiceService.closeInvoice(anyString(), anyString(), any(InvoiceDto.class)))
                .thenReturn(Mono.just(invoiceDto));

        StepVerifier.create(invoiceHandler.closeInvoice(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
