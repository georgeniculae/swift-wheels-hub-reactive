package com.swiftwheelshubreactive.agency.mapper;

import com.swiftwheelshubreactive.dto.BodyCategory;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.ExcelCarRequest;
import com.swiftwheelshubreactive.model.BodyType;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
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
    @Mapping(target = "image", expression = "java(mapByteArrayToBinary(imageContent))")
    Car mapDtoToEntity(CarRequest carRequest, byte[] imageContent);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    @Mapping(target = "image", expression = "java(mapByteArrayToBinary(excelCarRequest.image()))")
    Car mapExcelCarRequestToEntity(ExcelCarRequest excelCarRequest);

    Car getNewCarInstance(Car existingCar);

    default Binary mapByteArrayToBinary(byte[] bytes) {
        return new Binary(BsonBinarySubType.BINARY, bytes);
    }

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
