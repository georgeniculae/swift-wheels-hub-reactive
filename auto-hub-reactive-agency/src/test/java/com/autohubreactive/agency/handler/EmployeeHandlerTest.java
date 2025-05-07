package com.autohubreactive.agency.handler;

import com.autohubreactive.agency.service.EmployeeService;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.agency.validator.EmployeeRequestValidator;
import com.autohubreactive.dto.agency.EmployeeRequest;
import com.autohubreactive.dto.agency.EmployeeResponse;
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
class EmployeeHandlerTest {

    @InjectMocks
    private EmployeeHandler employeeHandler;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private EmployeeRequestValidator employeeRequestValidator;

    @Test
    void findAllEmployees_success() {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        List<EmployeeResponse> employeeDtoList = List.of(employeeResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.findAllEmployees()).thenReturn(Flux.fromIterable(employeeDtoList));

        employeeHandler.findAllEmployees(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllEmployees_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.findAllEmployees()).thenReturn(Flux.empty());

        employeeHandler.findAllEmployees(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findEmployeeById_success() {
        EmployeeResponse employeeDto =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeeById(anyString())).thenReturn(Mono.just(employeeDto));

        employeeHandler.findEmployeeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findEmployeeByFilter_success() {
        EmployeeResponse employeeDto =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Test")
                .build();

        when(employeeService.findEmployeeByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(employeeDto));

        employeeHandler.findEmployeeByFilterInsensitiveCase(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findEmployeeById_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeeById(anyString())).thenReturn(Mono.empty());

        employeeHandler.findEmployeeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findEmployeesByBranchId_success() {
        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        List<EmployeeResponse> employeeDtoList = List.of(employeeResponse);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeesByBranchId(anyString())).thenReturn(Flux.fromIterable(employeeDtoList));

        employeeHandler.findEmployeesByBranchId(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findEmployeesByBranchId_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeesByBranchId(anyString())).thenReturn(Flux.empty());

        employeeHandler.findEmployeesByBranchId(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countEmployees_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.countEmployees()).thenReturn(Mono.just(5L));

        employeeHandler.countEmployees(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveEmployee_success() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(employeeRequest));

        when(employeeRequestValidator.validateBody(any())).thenReturn(Mono.just(employeeRequest));
        when(employeeService.saveEmployee(any(EmployeeRequest.class))).thenReturn(Mono.just(employeeResponse));

        employeeHandler.saveEmployee(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateEmployee_success() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(employeeRequest));

        when(employeeRequestValidator.validateBody(any())).thenReturn(Mono.just(employeeRequest));
        when(employeeService.updateEmployee(anyString(), any(EmployeeRequest.class))).thenReturn(Mono.just(employeeResponse));

        employeeHandler.updateEmployee(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void deleteEmployeeById_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.DELETE)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.deleteEmployeeById(anyString())).thenReturn(Mono.empty());

        employeeHandler.deleteEmployeeById(serverRequest)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
