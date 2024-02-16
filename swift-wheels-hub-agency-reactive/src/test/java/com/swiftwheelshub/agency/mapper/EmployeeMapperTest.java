package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.model.Employee;
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
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse = employeeMapper.mapEntityToDto(employee);

        assertNotNull(employeeResponse);
        AssertionUtils.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(employeeMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        EmployeeRequest employeeRequest =
                TestUtils.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Employee employee = Assertions.assertDoesNotThrow(() -> employeeMapper.mapDtoToEntity(employeeRequest));

        assertNotNull(employeeRequest);
        AssertionUtils.assertEmployeeRequest(employee, employeeRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(employeeMapper.mapDtoToEntity(null));
    }

}
