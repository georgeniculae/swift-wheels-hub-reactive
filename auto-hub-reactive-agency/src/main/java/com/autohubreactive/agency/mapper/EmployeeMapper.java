package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.dto.EmployeeResponse;
import com.autohubreactive.model.agency.Branch;
import com.autohubreactive.model.agency.Employee;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface EmployeeMapper {

    @Mapping(target = "workingBranchId", expression = "java(mapObjectIdToString(employee.getWorkingBranch().getId()))")
    EmployeeResponse mapEntityToDto(Employee employee);

    @Mapping(target = "workingBranch", expression = "java(workingBranch)")
    Employee getNewEmployee(EmployeeRequest employeeRequest, Branch workingBranch);

    @Mapping(target = "id", expression = "java(existingEmployee.getId())")
    @Mapping(target = "firstName", expression = "java(updatedEmployeeRequest.firstName())")
    @Mapping(target = "lastName", expression = "java(updatedEmployeeRequest.lastName())")
    @Mapping(target = "jobPosition", expression = "java(updatedEmployeeRequest.jobPosition())")
    @Mapping(target = "workingBranch", source = "workingBranch")
    Employee getUpdatedEmployee(Employee existingEmployee, EmployeeRequest updatedEmployeeRequest, Branch workingBranch);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
