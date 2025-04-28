package com.autohubreactive.agency.handler;

import com.autohubreactive.agency.service.EmployeeService;
import com.autohubreactive.agency.validator.EmployeeRequestValidator;
import com.autohubreactive.dto.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class EmployeeHandler {

    private static final String ID = "id";
    private static final String FILTER = "filter";
    private final EmployeeService employeeService;
    private final EmployeeRequestValidator employeeRequestValidator;

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findAllEmployees(ServerRequest serverRequest) {
        return employeeService.findAllEmployees()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findEmployeeById(ServerRequest serverRequest) {
        return employeeService.findEmployeeById(serverRequest.pathVariable(ID))
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('user')")
    public Mono<ServerResponse> findEmployeesByBranchId(ServerRequest serverRequest) {
        return employeeService.findEmployeesByBranchId(serverRequest.pathVariable(ID))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> findEmployeeByFilterInsensitiveCase(ServerRequest serverRequest) {
        return employeeService.findEmployeeByFilterInsensitiveCase(serverRequest.pathVariable(FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> countEmployees(ServerRequest serverRequest) {
        return employeeService.countEmployees()
                .flatMap(numberOfEmployees -> ServerResponse.ok().bodyValue(numberOfEmployees));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> saveEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeRequest.class)
                .flatMap(employeeRequestValidator::validateBody)
                .flatMap(employeeService::saveEmployee)
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> updateEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeRequest.class)
                .flatMap(employeeRequestValidator::validateBody)
                .flatMap(employeeRequest -> employeeService.updateEmployee(
                                serverRequest.pathVariable(ID),
                                employeeRequest
                        )
                )
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse));
    }

    @PreAuthorize("hasRole('admin')")
    public Mono<ServerResponse> deleteEmployeeById(ServerRequest serverRequest) {
        return employeeService.deleteEmployeeById(serverRequest.pathVariable(ID))
                .then(ServerResponse.noContent().build());
    }

}
