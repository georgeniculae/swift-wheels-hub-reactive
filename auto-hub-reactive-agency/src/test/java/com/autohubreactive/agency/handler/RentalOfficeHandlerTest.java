package com.autohubreactive.agency.handler;

import com.autohubreactive.agency.service.RentalOfficeService;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.agency.validator.RentalOfficeRequestValidator;
import com.autohubreactive.dto.RentalOfficeRequest;
import com.autohubreactive.dto.RentalOfficeResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalOfficeHandlerTest {

    @InjectMocks
    private RentalOfficeHandler rentalOfficeHandler;

    @Mock
    private RentalOfficeService rentalOfficeService;

    @Mock
    private RentalOfficeRequestValidator rentalOfficeRequestValidator;

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        List<RentalOfficeResponse> rentalOfficeResponses = List.of(rentalOfficeResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(Flux.fromIterable(rentalOfficeResponses));

        rentalOfficeHandler.findAllRentalOffices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllRentalOfficesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(Flux.empty());

        rentalOfficeHandler.findAllRentalOffices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(rentalOfficeService.findRentalOfficeById(anyString())).thenReturn(Mono.just(rentalOfficeResponse));

        rentalOfficeHandler.findRentalOfficeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByIdTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(rentalOfficeService.findRentalOfficeById(anyString())).thenReturn(Mono.empty());

        rentalOfficeHandler.findRentalOfficeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findRentalOfficesByFilterInsensitiveCaseTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Test")
                .build();

        when(rentalOfficeService.findRentalOfficesByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOfficeResponse));

        rentalOfficeHandler.findRentalOfficesByFilterInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findRentalOfficesByFilterInsensitiveCaseTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Test")
                .build();

        when(rentalOfficeService.findRentalOfficesByFilterInsensitiveCase(anyString())).thenReturn(Flux.empty());

        rentalOfficeHandler.findRentalOfficesByFilterInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countRentalOfficesTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.countRentalOffices()).thenReturn(Mono.just(2L));

        rentalOfficeHandler.countRentalOffices(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(rentalOfficeRequest));

        when(rentalOfficeRequestValidator.validateBody(any())).thenReturn(Mono.just(rentalOfficeRequest));
        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeRequest.class))).thenReturn(Mono.just(rentalOfficeResponse));

        rentalOfficeHandler.saveRentalOffice(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(rentalOfficeRequest));

        when(rentalOfficeRequestValidator.validateBody(any())).thenReturn(Mono.just(rentalOfficeRequest));
        when(rentalOfficeService.updateRentalOffice(anyString(), any(RentalOfficeRequest.class)))
                .thenReturn(Mono.just(rentalOfficeResponse));

        rentalOfficeHandler.updateRentalOffice(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteRentalOfficeByIdTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(rentalOfficeService.deleteRentalOfficeById(anyString())).thenReturn(Mono.empty());

        rentalOfficeHandler.deleteRentalOfficeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
