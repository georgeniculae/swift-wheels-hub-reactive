package com.autohubreactive.agency.mapper;

import com.autohubreactive.dto.AvailableCarInfo;
import com.autohubreactive.dto.BodyCategory;
import com.autohubreactive.dto.CarRequest;
import com.autohubreactive.dto.CarResponse;
import com.autohubreactive.dto.CarState;
import com.autohubreactive.dto.ExcelCarRequest;
import com.autohubreactive.model.BodyType;
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

    @Mapping(target = "bodyType", expression = "java(mapToBodyType(carRequest.bodyCategory()))")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(carRequest.carState()))")
    Car mapDtoToEntity(CarRequest carRequest);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    Car mapExcelCarRequestToEntity(ExcelCarRequest excelCarRequest);

    @Mapping(target = "actualBranchId", expression = "java(mapObjectIdToString(car.getActualBranch().getId()))")
    AvailableCarInfo mapToAvailableCarInfo(Car car);

    Car getNewCarInstance(Car existingCar);

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
