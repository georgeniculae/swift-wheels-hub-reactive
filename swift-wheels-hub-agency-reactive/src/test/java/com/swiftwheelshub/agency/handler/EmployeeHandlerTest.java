package com.swiftwheelshub.agency.handler;

import com.swiftwheelshub.agency.service.EmployeeService;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.EmployeeDto;
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

    @Test
    void findAllEmployees_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        List<EmployeeDto> employeeDtoList = List.of(employeeDto);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.findAllEmployees()).thenReturn(Flux.fromIterable(employeeDtoList));

        StepVerifier.create(employeeHandler.findAllEmployees(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findAllEmployees_noResultReturned() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.findAllEmployees()).thenReturn(Flux.empty());

        StepVerifier.create(employeeHandler.findAllEmployees(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findEmployeeById_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeeById(anyString())).thenReturn(Mono.just(employeeDto));

        StepVerifier.create(employeeHandler.findEmployeeById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void findEmployeeByFilter_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("filter", "Test")
                .build();

        when(employeeService.findEmployeeByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(employeeDto));

        StepVerifier.create(employeeHandler.findEmployeeByFilterInsensitiveCase(serverRequest))
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

        StepVerifier.create(employeeHandler.findEmployeeById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void findEmployeesByBranchId_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        List<EmployeeDto> employeeDtoList = List.of(employeeDto);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .pathVariable("id", "64f361caf291ae086e179547")
                .build();

        when(employeeService.findEmployeesByBranchId(anyString())).thenReturn(Flux.fromIterable(employeeDtoList));

        StepVerifier.create(employeeHandler.findEmployeesByBranchId(serverRequest))
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

        StepVerifier.create(employeeHandler.findEmployeesByBranchId(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is4xxClientError())
                .verifyComplete();
    }

    @Test
    void countEmployees_success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.GET)
                .build();

        when(employeeService.countEmployees()).thenReturn(Mono.just(5L));

        StepVerifier.create(employeeHandler.countEmployees(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void saveEmployee_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.POST)
                .body(Mono.just(employeeDto));

        when(employeeService.saveEmployee(any(EmployeeDto.class))).thenReturn(Mono.just(employeeDto));

        StepVerifier.create(employeeHandler.saveEmployee(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void updateEmployee_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);

        ServerRequest serverRequest = MockServerRequest.builder()
                .method(HttpMethod.PUT)
                .pathVariable("id", "64f361caf291ae086e179547")
                .body(Mono.just(employeeDto));

        when(employeeService.updateEmployee(anyString(), any(EmployeeDto.class))).thenReturn(Mono.just(employeeDto));

        StepVerifier.create(employeeHandler.updateEmployee(serverRequest))
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

        StepVerifier.create(employeeHandler.deleteEmployeeById(serverRequest))
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

}
