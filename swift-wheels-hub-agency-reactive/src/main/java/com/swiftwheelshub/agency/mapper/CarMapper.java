package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.BodyCategory;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
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
    CarResponse mapEntityToDto(Car car);

    Car mapDtoToEntity(CarRequest carRequest);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

    default BodyCategory mapToBodyCategory(BodyType bodyType) {
        return switch (bodyType) {
            case HATCHBACK -> BodyCategory.HATCHBACK;
            case SEDAN -> BodyCategory.SEDAN;
            case SUV -> BodyCategory.SUV;
            case COUPE -> BodyCategory.COUPE;
            case CONVERTIBLE -> BodyCategory.CONVERTIBLE;
            case WAGON -> BodyCategory.WAGON;
            case VAN -> BodyCategory.VAN;
        };
    }

    default BodyType mapToBodyType(BodyCategory bodyCategory) {
        return switch (bodyCategory) {
            case HATCHBACK -> BodyType.HATCHBACK;
            case SEDAN -> BodyType.SEDAN;
            case SUV -> BodyType.SUV;
            case COUPE -> BodyType.COUPE;
            case CONVERTIBLE -> BodyType.CONVERTIBLE;
            case WAGON -> BodyType.WAGON;
            case VAN -> BodyType.VAN;
        };
    }

    default CarState mapToCarState(CarStatus carStatus) {
        return switch (carStatus) {
            case NOT_AVAILABLE -> CarState.NOT_AVAILABLE;
            case BROKEN -> CarState.BROKEN;
            case IN_REPAIR -> CarState.IN_REPAIR;
            case IN_SERVICE -> CarState.IN_SERVICE;
            case AVAILABLE -> CarState.AVAILABLE;
        };
    }

    default CarStatus mapToCarStatus(CarState carStatusEnum) {
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
