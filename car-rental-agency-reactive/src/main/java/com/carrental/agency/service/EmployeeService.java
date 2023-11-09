package com.carrental.agency.service;

import com.carrental.agency.mapper.EmployeeMapper;
import com.carrental.agency.repository.EmployeeRepository;
import com.carrental.document.model.Employee;
import com.carrental.dto.EmployeeDto;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.util.MongoUtil;
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

    public Flux<EmployeeDto> findAllEmployees() {
        return employeeRepository.findAll()
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all employees: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<EmployeeDto> findEmployeeById(String id) {
        return findEntityById(id)
                .map(employeeMapper::mapEntityToDto).onErrorResume(e -> {
                    log.error("Error while finding employee by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });

    }

    public Mono<EmployeeDto> saveEmployee(EmployeeDto employeeDto) {
        return branchService.findEntityById(employeeDto.getWorkingBranchId())
                .flatMap(workingBranch -> {
                    Employee newEmployee = employeeMapper.mapDtoToEntity(employeeDto);
                    newEmployee.setWorkingBranch(workingBranch);

                    return employeeRepository.save(newEmployee);
                })
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving employee: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<EmployeeDto> updateEmployee(String id, EmployeeDto updatedEmployeeDto) {
        return findEntityById(id)
                .flatMap(existingEmployee -> {
                    String workingBranchId = updatedEmployeeDto.getWorkingBranchId();

                    return branchService.findEntityById(workingBranchId)
                            .flatMap(workingBranch -> {
                                existingEmployee.setFirstName(updatedEmployeeDto.getFirstName());
                                existingEmployee.setLastName(updatedEmployeeDto.getLastName());
                                existingEmployee.setJobPosition(updatedEmployeeDto.getJobPosition());
                                existingEmployee.setWorkingBranch(workingBranch);

                                return employeeRepository.save(existingEmployee);
                            });
                })
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating employee: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<EmployeeDto> findEmployeesByBranchId(String id) {
        return employeeRepository.findAllEmployeesByBranchId(MongoUtil.getObjectId(id))
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all employees ny branch id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<EmployeeDto> findEmployeeByFilterInsensitiveCase(String searchString) {
        return employeeRepository.findAllByFilterInsensitiveCase(searchString)
                .map(employeeMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding employee by filter: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
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

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Void> deleteEmployeeById(String id) {
        return employeeRepository.deleteById(MongoUtil.getObjectId(id)).onErrorResume(e -> {
            log.error("Error while deleting employee: {}", e.getMessage());

            return Mono.error(new CarRentalException(e.getMessage()));
        });
    }

    public Mono<Employee> findEntityById(String id) {
        return employeeRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Employee with id " + id + " does not exist"
                                )
                        )
                );
    }

}
