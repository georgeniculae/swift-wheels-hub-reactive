package com.swiftwheelshubreactive.agency.mapper;

import com.swiftwheelshubreactive.dto.BranchRequest;
import com.swiftwheelshubreactive.dto.BranchResponse;
import com.swiftwheelshubreactive.model.Branch;
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

    Branch getNewBranchInstance(Branch existingBranch);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
