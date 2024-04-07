package com.swiftwheelshubreactive.agency.handler;

import com.swiftwheelshubreactive.agency.service.EmployeeService;
import com.swiftwheelshubreactive.agency.validator.EmployeeRequestValidator;
import com.swiftwheelshubreactive.dto.EmployeeRequest;
import com.swiftwheelshubreactive.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.access.annotation.Secured;
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

    @Secured("admin")
    public Mono<ServerResponse> findAllEmployees(ServerRequest serverRequest) {
        return employeeService.findAllEmployees()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Secured("user")
    public Mono<ServerResponse> findEmployeeById(ServerRequest serverRequest) {
        return employeeService.findEmployeeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Secured("user")
    public Mono<ServerResponse> findEmployeesByBranchId(ServerRequest serverRequest) {
        return employeeService.findEmployeesByBranchId(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Secured("admin")
    public Mono<ServerResponse> findEmployeeByFilterInsensitiveCase(ServerRequest serverRequest) {
        return employeeService.findEmployeeByFilterInsensitiveCase(ServerRequestUtil.getPathVariable(serverRequest, FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeResponses -> ServerResponse.ok().bodyValue(employeeResponses))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    @Secured("admin")
    public Mono<ServerResponse> countEmployees(ServerRequest serverRequest) {
        return employeeService.countEmployees()
                .flatMap(numberOfEmployees -> ServerResponse.ok().bodyValue(numberOfEmployees));
    }

    @Secured("admin")
    public Mono<ServerResponse> saveEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeRequest.class)
                .flatMap(employeeRequestValidator::validateBody)
                .flatMap(employeeService::saveEmployee)
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse));
    }

    @Secured("admin")
    public Mono<ServerResponse> updateEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeRequest.class)
                .flatMap(employeeRequestValidator::validateBody)
                .flatMap(employeeRequest -> employeeService.updateEmployee(ServerRequestUtil.getPathVariable(serverRequest, ID), employeeRequest))
                .flatMap(employeeResponse -> ServerResponse.ok().bodyValue(employeeResponse));
    }

    @Secured("admin")
    public Mono<ServerResponse> deleteEmployeeById(ServerRequest serverRequest) {
        return employeeService.deleteEmployeeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.noContent().build());
    }

}
