package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.RentalOfficeRequest;
import com.autohubreactive.dto.RentalOfficeResponse;
import com.autohubreactive.model.RentalOffice;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RentalOfficeMapper {

    RentalOfficeResponse mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice mapDtoToEntity(RentalOfficeRequest rentalOfficeRequest);

    RentalOffice getNewRentalOfficeInstance(RentalOffice existingRentalOffice);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
