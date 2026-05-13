package com.autohubreactive.invoicenotification.mapper;

import com.autohubreactive.dto.invoicenotification.EmailResponse;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface EmailResponseMapper {

    @Mapping(source = "errorCode", target = "statusCode")
    @Mapping(source = "message", target = "body")
    EmailResponse mapToEmailResponse(MessageResponse response);

}
