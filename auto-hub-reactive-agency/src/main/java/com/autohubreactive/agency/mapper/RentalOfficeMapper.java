package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.agency.RentalOfficeRequest;
import com.autohubreactive.dto.agency.RentalOfficeResponse;
import com.autohubreactive.model.agency.RentalOffice;
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
public interface RentalOfficeMapper {

    RentalOfficeResponse mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice getNewRentalOffice(RentalOfficeRequest rentalOfficeRequest);

    @Mapping(target = "name", expression = "java(updatedRentalOfficeRequest.name())")
    @Mapping(target = "contactAddress", expression = "java(updatedRentalOfficeRequest.contactAddress())")
    @Mapping(target = "phoneNumber", expression = "java(updatedRentalOfficeRequest.phoneNumber())")
    RentalOffice getUpdatedRentalOffice(RentalOffice existingRentalOffice, RentalOfficeRequest updatedRentalOfficeRequest);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
