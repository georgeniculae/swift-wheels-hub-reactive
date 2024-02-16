package com.swiftwheelshub.agency.handler;

import com.swiftwheelshub.agency.service.RentalOfficeService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
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

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        List<RentalOfficeResponse> rentalOfficeResponses = List.of(rentalOfficeResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(Flux.fromIterable(rentalOfficeResponses));

        StepVerifier.create(rentalOfficeHandler.findAllRentalOffices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllRentalOfficesTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(Flux.empty());

        StepVerifier.create(rentalOfficeHandler.findAllRentalOffices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(rentalOfficeService.findRentalOfficeById(anyString())).thenReturn(Mono.just(rentalOfficeResponse));

        StepVerifier.create(rentalOfficeHandler.findRentalOfficeById(serverRequest))
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

        StepVerifier.create(rentalOfficeHandler.findRentalOfficeById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("name", "Test")
                .build();

        when(rentalOfficeService.findRentalOfficesByNameInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOfficeResponse));

        StepVerifier.create(rentalOfficeHandler.findRentalOfficesByNameInsensitiveCase(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findRentalOfficeByNameTest_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("name", "Test")
                .build();

        when(rentalOfficeService.findRentalOfficesByNameInsensitiveCase(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(rentalOfficeHandler.findRentalOfficesByNameInsensitiveCase(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countRentalOfficesTest_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.countRentalOffices()).thenReturn(Mono.just(2L));

        StepVerifier.create(rentalOfficeHandler.countRentalOffices(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(rentalOfficeResponse));

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeRequest.class))).thenReturn(Mono.just(rentalOfficeResponse));

        StepVerifier.create(rentalOfficeHandler.saveRentalOffice(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtils.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(rentalOfficeResponse));

        when(rentalOfficeService.updateRentalOffice(anyString(), any(RentalOfficeRequest.class)))
                .thenReturn(Mono.just(rentalOfficeResponse));

        StepVerifier.create(rentalOfficeHandler.updateRentalOffice(serverRequest))
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

        StepVerifier.create(rentalOfficeHandler.deleteRentalOfficeById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
