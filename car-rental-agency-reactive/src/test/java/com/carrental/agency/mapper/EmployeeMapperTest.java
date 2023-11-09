package com.carrental.agency.mapper;

import com.carrental.agency.util.AssertionUtils;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.Employee;
import com.carrental.dto.EmployeeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeDto employeeDto = assertDoesNotThrow(() -> employeeMapper.mapEntityToDto(employee));

        assertNotNull(employeeDto);
        AssertionUtils.assertEmployee(employee, employeeDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        EmployeeDto employeeDto = assertDoesNotThrow(() -> employeeMapper.mapEntityToDto(null));

        assertNull(employeeDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);

        Employee employee = assertDoesNotThrow(() -> employeeMapper.mapDtoToEntity(employeeDto));

        assertNotNull(employeeDto);
        AssertionUtils.assertEmployee(employee, employeeDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Employee employee = assertDoesNotThrow(() -> employeeMapper.mapDtoToEntity(null));

        assertNull(employee);
    }

}
