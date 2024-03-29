package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.RevenueResponse;
import com.swiftwheelshubreactive.model.Revenue;
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
