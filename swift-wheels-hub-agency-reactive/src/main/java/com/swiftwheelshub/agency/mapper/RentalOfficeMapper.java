package com.swiftwheelshub.agency.mapper;

import com.carrental.document.model.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RentalOfficeMapper {

    RentalOfficeDto mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice mapDtoToEntity(RentalOfficeDto rentalOfficeDto);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
