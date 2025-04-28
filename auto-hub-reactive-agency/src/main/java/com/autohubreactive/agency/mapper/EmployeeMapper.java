package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.EmployeeRequest;
import com.autohubreactive.dto.EmployeeResponse;
import com.autohubreactive.model.Employee;
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

    Employee mapDtoToEntity(EmployeeRequest employeeRequest);

    Employee getNewEmployeeInstance(Employee existingEmployee);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
