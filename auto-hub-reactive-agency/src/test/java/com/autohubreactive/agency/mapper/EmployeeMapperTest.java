package com.autohubreactive.agency.mapper;

import com.autohubreactive.agency.util.AssertionUtil;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.dto.EmployeeResponse;
import com.autohubreactive.model.agency.Branch;
import com.autohubreactive.model.agency.Employee;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse = employeeMapper.mapEntityToDto(employee);

        assertNotNull(employeeResponse);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(employeeMapper.mapEntityToDto(null));
    }

    @Test
    void getNewEmployeeTest_success() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch workingBranch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        Employee employee = Assertions.assertDoesNotThrow(() -> employeeMapper.getNewEmployee(employeeRequest, workingBranch));

        assertNotNull(employeeRequest);
        AssertionUtil.assertEmployeeRequest(employee, employeeRequest);
    }

    @Test
    void getNewEmployeeTest_null() {
        assertNull(employeeMapper.getNewEmployee(null, null));
    }

}
