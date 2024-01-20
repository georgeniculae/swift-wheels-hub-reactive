package com.swiftwheelshub.agency.handler;

import com.swiftwheelshub.agency.service.RentalOfficeService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.carrental.dto.RentalOfficeDto;
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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);
        List<RentalOfficeDto> rentalOfficeDtoList = List.of(rentalOfficeDto);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(Flux.fromIterable(rentalOfficeDtoList));

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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(rentalOfficeService.findRentalOfficeById(anyString())).thenReturn(Mono.just(rentalOfficeDto));

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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("name", "Test")
                .build();

        when(rentalOfficeService.findRentalOfficesByNameInsensitiveCase(anyString())).thenReturn(Flux.just(rentalOfficeDto));

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
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(rentalOfficeDto));

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeDto.class))).thenReturn(Mono.just(rentalOfficeDto));

        StepVerifier.create(rentalOfficeHandler.saveRentalOffice(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOfficeDto rentalOfficeDto =
                TestUtils.getResourceAsJson("/data/RentalOfficeDto.json", RentalOfficeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(rentalOfficeDto));

        when(rentalOfficeService.updateRentalOffice(anyString(), any(RentalOfficeDto.class)))
                .thenReturn(Mono.just(rentalOfficeDto));

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
