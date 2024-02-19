package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.model.Revenue;
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
public interface RevenueMapper {

    RevenueResponse mapEntityToDto(Revenue revenue);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

}
