package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.EmployeeMapper;
import com.autohubreactive.agency.mapper.EmployeeMapperImpl;
import com.autohubreactive.agency.repository.EmployeeRepository;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.dto.EmployeeResponse;
import com.autohubreactive.model.Branch;
import com.autohubreactive.model.Employee;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BranchService branchService;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void findAllEmployeesTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        List<Employee> employees = List.of(employee);

        when(employeeRepository.findAll()).thenReturn(Flux.fromIterable(employees));

        employeeService.findAllEmployees()
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();
    }

    @Test
    void findAllEmployeesTest_errorOnFindAll() {
        when(employeeRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        employeeService.findAllEmployees()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findEmployeeByIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(employee));

        employeeService.findEmployeeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();
    }

    @Test
    void findEmployeeByIdTest_errorOnFindingById() {
        when(employeeRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        employeeService.findEmployeeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countEmployeesTest_success() {
        when(employeeRepository.count()).thenReturn(Mono.just(3L));

        employeeService.countEmployees()
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countEmployeesTest_errorOnCounting() {
        when(employeeRepository.count()).thenReturn(Mono.error(new Throwable()));

        employeeService.countEmployees()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(employee));

        employeeService.saveEmployee(employeeRequest)
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();

        verify(employeeMapper).mapEntityToDto(any(Employee.class));
    }

    @Test
    void saveEmployeeTest_errorOnSave() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.error(new Throwable()));

        employeeService.saveEmployee(employeeRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();

        verify(employeeMapper, never()).mapEntityToDto(any(Employee.class));
    }

    @Test
    void updateEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(employeeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.just(employee));

        employeeService.updateEmployee("64f361caf291ae086e179547", employeeRequest)
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();
    }

    @Test
    void updateEmployeeTest_errorOnSave() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(employeeRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(Mono.error(new Throwable()));

        employeeService.updateEmployee("64f361caf291ae086e179547", employeeRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findEmployeesByBranchIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        List<Employee> employees = List.of(employee);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeRepository.findAllEmployeesByBranchId(any(ObjectId.class))).thenReturn(Flux.fromIterable(employees));

        employeeService.findEmployeesByBranchId("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();
    }

    @Test
    void findEmployeesByBranchIdTest_errorOnFindingByBranchId() {
        when(employeeRepository.findAllEmployeesByBranchId(any(ObjectId.class))).thenReturn(Flux.error(new Throwable()));

        employeeService.findEmployeesByBranchId("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findEmployeeByFilterTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse =
                TestUtil.getResourceAsJson("/data/EmployeeResponse.json", EmployeeResponse.class);

        when(employeeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(employee));

        employeeService.findEmployeeByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectNext(employeeResponse)
                .verifyComplete();
    }

    @Test
    void findEmployeeByFilterTest_errorWhileFindingByFilter() {
        when(employeeRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        employeeService.findEmployeeByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteEmployeeByIdTest_success() {
        when(employeeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        employeeService.deleteEmployeeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteEmployeeByIdTest_errorOnDeletingById() {
        when(employeeRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        employeeService.deleteEmployeeById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
