package com.swiftwheelshubreactive.emailnotification.mapper;

import com.sendgrid.Response;
import com.swiftwheelshubreactive.dto.EmailResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface EmailResponseMapper {

    EmailResponse mapToEmailResponse(Response response);

}
