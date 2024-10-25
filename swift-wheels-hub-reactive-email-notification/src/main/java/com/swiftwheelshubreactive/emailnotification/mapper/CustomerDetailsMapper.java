package com.swiftwheelshubreactive.emailnotification.mapper;

import com.swiftwheelshubreactive.dto.CustomerInfo;
import com.swiftwheelshubreactive.emailnotification.model.CustomerDetails;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CustomerDetailsMapper {

    CustomerDetails mapToCustomerDetails(CustomerInfo customerInfo);

}
