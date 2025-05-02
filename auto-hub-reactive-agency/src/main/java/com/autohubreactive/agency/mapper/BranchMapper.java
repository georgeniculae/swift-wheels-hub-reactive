package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.BranchRequest;
import com.autohubreactive.dto.BranchResponse;
import com.autohubreactive.model.Branch;
import com.autohubreactive.model.RentalOffice;
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

    @Mapping(target = "rentalOffice", expression = "java(rentalOffice)")
    @Mapping(target = "name", expression = "java(branchRequest.name())")
    Branch getNewBranch(BranchRequest branchRequest, RentalOffice rentalOffice);

    @Mapping(target = "id", expression = "java(existingBranch.getId())")
    @Mapping(target = "name", expression = "java(branchRequest.name())")
    @Mapping(target = "address", expression = "java(branchRequest.address())")
    @Mapping(target = "rentalOffice", source = "rentalOffice")
    Branch getUpdatedBranch(Branch existingBranch, BranchRequest branchRequest, RentalOffice rentalOffice);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
