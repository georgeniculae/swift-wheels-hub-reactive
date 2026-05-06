package com.autohubreactive.agency.mapper;

import com.autohubreactive.agency.entity.BodyType;
import com.autohubreactive.agency.entity.Car;
import com.autohubreactive.agency.entity.CarStatus;
import com.autohubreactive.agency.entity.RentalOffice;
import com.autohubreactive.dto.agency.BodyCategory;
import com.autohubreactive.dto.agency.CarRequest;
import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.dto.agency.ExcelCarRequest;
import com.autohubreactive.dto.ai.AvailableCarDetails;
import com.autohubreactive.dto.common.AvailableCarInfo;
import com.autohubreactive.dto.common.CarState;
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
public interface CarMapper {

    @Mapping(target = "initialRentalOfficeId", expression = "java(mapObjectIdToString(car.initialRentalOffice().id()))")
    @Mapping(target = "actualRentalOfficeId", expression = "java(mapObjectIdToString(car.actualRentalOffice().id()))")
    @Mapping(target = "carLocation", expression = "java(car.actualRentalOffice().city())")
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarResponse mapEntityToDto(Car car);

    @Mapping(target = "carLocation", expression = "java(car.actualRentalOffice().city())")
    @Mapping(target = "bodyCategory", source = "bodyType")
    AvailableCarDetails mapEntityToAvailableCarDetails(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bodyType", expression = "java(mapToBodyType(carRequest.bodyCategory()))")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(carRequest.carState()))")
    @Mapping(target = "initialRentalOffice", expression = "java(initialRentalOffice)")
    @Mapping(target = "actualRentalOffice", expression = "java(actualRentalOffice)")
    Car getNewCar(CarRequest carRequest, RentalOffice initialRentalOffice, RentalOffice actualRentalOffice);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    Car mapExcelCarRequestToEntity(ExcelCarRequest excelCarRequest);

    @Mapping(target = "carStatus", source = "carStatus")
    Car getUpdatedCarWithStatus(Car existingCar, CarStatus carStatus);

    @Mapping(target = "actualRentalOfficeId", expression = "java(mapObjectIdToString(car.actualRentalOffice().id()))")
    AvailableCarInfo mapToAvailableCarInfo(Car car);

    @Mapping(target = "id", expression = "java(existingCarId)")
    @Mapping(target = "make", expression = "java(updatedCarRequest.make())")
    @Mapping(target = "model", expression = "java(updatedCarRequest.model())")
    @Mapping(target = "bodyType", expression = "java(mapToBodyType(updatedCarRequest.bodyCategory()))")
    @Mapping(target = "yearOfProduction", expression = "java(updatedCarRequest.yearOfProduction())")
    @Mapping(target = "color", expression = "java(updatedCarRequest.color())")
    @Mapping(target = "mileage", expression = "java(updatedCarRequest.mileage())")
    @Mapping(target = "amount", expression = "java(updatedCarRequest.amount())")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(updatedCarRequest.carState()))")
    @Mapping(target = "initialRentalOffice", expression = "java(initialRentalOffice)")
    @Mapping(target = "actualRentalOffice", expression = "java(actualRentalOffice)")
    Car getUpdatedCar(
            ObjectId existingCarId,
            CarRequest updatedCarRequest,
            RentalOffice initialRentalOffice,
            RentalOffice actualRentalOffice
    );

    @Mapping(target = "id", expression = "java(car.id())")
    @Mapping(target = "actualRentalOffice", expression = "java(workingRentalOffice)")
    @Mapping(target = "carStatus", source = "carStatus")
    Car getCarAfterBookingClosing(Car car, RentalOffice workingRentalOffice, CarStatus carStatus);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default BodyType mapToBodyType(BodyCategory bodyCategory) {
        return BodyType.valueOf(bodyCategory.name());
    }

    default CarStatus mapToCarStatus(CarState carState) {
        return CarStatus.valueOf(carState.name());
    }

}
