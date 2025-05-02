package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.AvailableCarInfo;
import com.autohubreactive.dto.BodyCategory;
import com.autohubreactive.dto.CarRequest;
import com.autohubreactive.dto.CarResponse;
import com.autohubreactive.dto.CarState;
import com.autohubreactive.dto.ExcelCarRequest;
import com.autohubreactive.model.BodyType;
import com.autohubreactive.model.Branch;
import com.autohubreactive.model.Car;
import com.autohubreactive.model.CarStatus;
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

    @Mapping(target = "originalBranchId", expression = "java(mapObjectIdToString(car.getOriginalBranch().getId()))")
    @Mapping(target = "actualBranchId", expression = "java(mapObjectIdToString(car.getActualBranch().getId()))")
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarResponse mapEntityToDto(Car car);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bodyType", expression = "java(mapToBodyType(carRequest.bodyCategory()))")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(carRequest.carState()))")
    @Mapping(target = "originalBranch", expression = "java(originalBranch)")
    @Mapping(target = "actualBranch", expression = "java(actualBranch)")
    Car getNewCar(CarRequest carRequest, Branch originalBranch, Branch actualBranch);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    Car mapExcelCarRequestToEntity(ExcelCarRequest excelCarRequest);

    @Mapping(target = "carStatus", source = "carStatus")
    Car getUpdatedCarWithStatus(Car existingCar, CarStatus carStatus);

    @Mapping(target = "actualBranchId", expression = "java(mapObjectIdToString(car.getActualBranch().getId()))")
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
    @Mapping(target = "originalBranch", expression = "java(originalBranch)")
    @Mapping(target = "actualBranch", expression = "java(actualBranch)")
    Car getUpdatedCar(ObjectId existingCarId, CarRequest updatedCarRequest, Branch originalBranch, Branch actualBranch);

    @Mapping(target = "id", expression = "java(car.getId())")
    @Mapping(target = "actualBranch", expression = "java(workingBranch)")
    @Mapping(target = "carStatus", source = "carStatus")
    Car getCarAfterBookingClosing(Car car, Branch workingBranch, CarStatus carStatus);

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
