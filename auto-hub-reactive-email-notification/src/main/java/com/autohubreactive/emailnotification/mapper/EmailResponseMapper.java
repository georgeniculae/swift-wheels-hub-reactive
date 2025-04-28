package com.autohubreactive.emailnotification.mapper;

import com.sendgrid.Response;
import com.autohubreactive.dto.EmailResponse;
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
