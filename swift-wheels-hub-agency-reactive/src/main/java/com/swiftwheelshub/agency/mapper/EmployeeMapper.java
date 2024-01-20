package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.model.Employee;
import com.carrental.dto.EmployeeDto;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmployeeMapper {

    @Mapping(target = "workingBranchId", expression = "java(mapObjectIdToString(employee.getWorkingBranch().getId()))")
    EmployeeDto mapEntityToDto(Employee employee);

    Employee mapDtoToEntity(EmployeeDto employeeDto);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
