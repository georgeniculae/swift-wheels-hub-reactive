package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.dto.CarStatusEnum;
import com.swiftwheelshub.model.BodyType;
import com.swiftwheelshub.model.Car;
import com.swiftwheelshub.model.CarStatus;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CarMapper {

    @Mapping(target = "originalBranchId", expression = "java(mapObjectIdToString(car.getOriginalBranch().getId()))")
    @Mapping(target = "actualBranchId", expression = "java(mapObjectIdToString(car.getActualBranch().getId()))")
    CarDto mapEntityToDto(Car car);

    Car mapDtoToEntity(CarDto carDto);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

    default CarDto.BodyTypeEnum mapToBodyTypeEnum(BodyType bodyType) {
        return switch (bodyType) {
            case HATCHBACK -> CarDto.BodyTypeEnum.HATCHBACK;
            case SEDAN -> CarDto.BodyTypeEnum.SEDAN;
            case SUV -> CarDto.BodyTypeEnum.SUV;
            case COUPE -> CarDto.BodyTypeEnum.COUPE;
            case CONVERTIBLE -> CarDto.BodyTypeEnum.CONVERTIBLE;
            case WAGON -> CarDto.BodyTypeEnum.WAGON;
            case VAN -> CarDto.BodyTypeEnum.VAN;
        };
    }

    default BodyType mapToBodyType(CarDto.BodyTypeEnum bodyTypeEnum) {
        return switch (bodyTypeEnum) {
            case HATCHBACK -> BodyType.HATCHBACK;
            case SEDAN -> BodyType.SEDAN;
            case SUV -> BodyType.SUV;
            case COUPE -> BodyType.COUPE;
            case CONVERTIBLE -> BodyType.CONVERTIBLE;
            case WAGON -> BodyType.WAGON;
            case VAN -> BodyType.VAN;
        };
    }

    default CarStatusEnum mapToCarStatusEnum(CarStatus carStatus) {
        return switch (carStatus) {
            case NOT_AVAILABLE -> CarStatusEnum.NOT_AVAILABLE;
            case BROKEN -> CarStatusEnum.BROKEN;
            case IN_REPAIR -> CarStatusEnum.IN_REPAIR;
            case IN_SERVICE -> CarStatusEnum.IN_SERVICE;
            case AVAILABLE -> CarStatusEnum.AVAILABLE;
        };
    }

    default CarStatus mapToCarStatus(CarStatusEnum carStatusEnum) {
        return switch (carStatusEnum) {
            case NOT_AVAILABLE -> CarStatus.NOT_AVAILABLE;
            case BROKEN -> CarStatus.BROKEN;
            case IN_REPAIR -> CarStatus.IN_REPAIR;
            case IN_SERVICE -> CarStatus.IN_SERVICE;
            case AVAILABLE -> CarStatus.AVAILABLE;
        };
    }

    default Double mapBigDecimalToDouble(BigDecimal bigDecimal) {
        return bigDecimal.doubleValue();
    }

}
