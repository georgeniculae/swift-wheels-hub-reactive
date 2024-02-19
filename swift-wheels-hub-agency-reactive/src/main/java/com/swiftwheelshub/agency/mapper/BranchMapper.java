package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.model.Branch;
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
public interface BranchMapper {

    @Mapping(target = "rentalOfficeId", expression = "java(mapObjectIdToString(branch.getRentalOffice().getId()))")
    BranchResponse mapEntityToDto(Branch branch);

    Branch mapDtoToEntity(BranchRequest branchRequest);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
