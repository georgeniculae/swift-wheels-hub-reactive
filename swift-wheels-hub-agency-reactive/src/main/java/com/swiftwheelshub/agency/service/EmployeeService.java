package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.EmployeeMapper;
import com.swiftwheelshub.agency.repository.EmployeeRepository;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchService branchService;
    private final EmployeeMapper employeeMapper;

    public Flux<EmployeeResponse> findAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all employees: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<EmployeeResponse> findEmployeeById(String id) {
        return findEntityById(id)
                .map(employeeMapper::mapEntityToDto).onErrorResume(e -> {
                    log.error("Error while finding employee by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });

    }

    public Mono<EmployeeResponse> saveEmployee(EmployeeRequest employeeRequest) {
        return branchService.findEntityById(employeeRequest.workingBranchId())
                .flatMap(workingBranch -> {
                    Employee newEmployee = employeeMapper.mapDtoToEntity(employeeRequest);
                    newEmployee.setWorkingBranch(workingBranch);

                    return employeeRepository.save(newEmployee);
                })
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving employee: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<EmployeeResponse> updateEmployee(String id, EmployeeRequest updatedEmployeeRequest) {
        return findEntityById(id)
                .flatMap(existingEmployee -> {
                    String workingBranchId = updatedEmployeeRequest.workingBranchId();

                    return branchService.findEntityById(workingBranchId)
                            .flatMap(workingBranch -> {
                                existingEmployee.setFirstName(updatedEmployeeRequest.firstName());
                                existingEmployee.setLastName(updatedEmployeeRequest.lastName());
                                existingEmployee.setJobPosition(updatedEmployeeRequest.jobPosition());
                                existingEmployee.setWorkingBranch(workingBranch);

                                return employeeRepository.save(existingEmployee);
                            });
                })
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating employee: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<EmployeeResponse> findEmployeesByBranchId(String id) {
        return employeeRepository.findAllEmployeesByBranchId(MongoUtil.getObjectId(id))
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all employees ny branch id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<EmployeeResponse> findEmployeeByFilterInsensitiveCase(String searchString) {
        return employeeRepository.findAllByFilterInsensitiveCase(searchString)
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding employee by filter: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Employee with filter: " + searchString + " does not exist"
                                )
                        )
                );
    }

    public Mono<Long> countEmployees() {
        return employeeRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting employees: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Void> deleteEmployeeById(String id) {
        return employeeRepository.deleteById(MongoUtil.getObjectId(id)).onErrorResume(e -> {
            log.error("Error while deleting employee: {}", e.getMessage());

            return Mono.error(new SwiftWheelsHubException(e.getMessage()));
        });
    }

    public Mono<Employee> findEntityById(String id) {
        return employeeRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Employee with id " + id + " does not exist"
                                )
                        )
                );
    }

}
