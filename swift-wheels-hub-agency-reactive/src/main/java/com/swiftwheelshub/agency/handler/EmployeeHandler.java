package com.swiftwheelshub.agency.handler;

import com.swiftwheelshub.agency.service.EmployeeService;
import com.carrental.dto.EmployeeDto;
import com.carrental.lib.util.ServerRequestUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
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

    public Mono<ServerResponse> findAllEmployees(ServerRequest serverRequest) {
        return employeeService.findAllEmployees()
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeDtoList -> ServerResponse.ok().bodyValue(employeeDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findEmployeeById(ServerRequest serverRequest) {
        return employeeService.findEmployeeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .flatMap(employeeDto -> ServerResponse.ok().bodyValue(employeeDto))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findEmployeesByBranchId(ServerRequest serverRequest) {
        return employeeService.findEmployeesByBranchId(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeDtoList -> ServerResponse.ok().bodyValue(employeeDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> findEmployeeByFilterInsensitiveCase(ServerRequest serverRequest) {
        return employeeService.findEmployeeByFilterInsensitiveCase(ServerRequestUtil.getPathVariable(serverRequest, FILTER))
                .collectList()
                .filter(ObjectUtils::isNotEmpty)
                .flatMap(employeeDtoList -> ServerResponse.ok().bodyValue(employeeDtoList))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> countEmployees(ServerRequest serverRequest) {
        return employeeService.countEmployees()
                .flatMap(numberOfEmployees -> ServerResponse.ok().bodyValue(numberOfEmployees));
    }

    public Mono<ServerResponse> saveEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeDto.class)
                .flatMap(employeeService::saveEmployee)
                .flatMap(employeeDto -> ServerResponse.ok().bodyValue(employeeDto));
    }

    public Mono<ServerResponse> updateEmployee(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EmployeeDto.class)
                .flatMap(employeeDto ->
                        employeeService.updateEmployee(ServerRequestUtil.getPathVariable(serverRequest, ID), employeeDto))
                .flatMap(employeeDto -> ServerResponse.ok().bodyValue(employeeDto));
    }

    public Mono<ServerResponse> deleteEmployeeById(ServerRequest serverRequest) {
        return employeeService.deleteEmployeeById(ServerRequestUtil.getPathVariable(serverRequest, ID))
                .then(ServerResponse.noContent().build());
    }

}
