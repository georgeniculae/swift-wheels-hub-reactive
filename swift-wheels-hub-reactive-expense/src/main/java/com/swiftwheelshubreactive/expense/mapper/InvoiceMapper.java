package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.model.Invoice;
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
public interface InvoiceMapper {

    InvoiceResponse mapEntityToDto(Invoice invoice);

    Invoice mapDtoToEntity(InvoiceRequest invoiceRequest);

    Invoice getNewInvoiceInstance(Invoice existingInvoice);

    @Mapping(target = "invoiceProcessStatus", constant = "SAVED_CLOSED_INVOICE")
    Invoice mapRequestToSuccessfulInvoice(InvoiceRequest invoiceRequest);

    @Mapping(target = "invoiceProcessStatus", constant = "SAVED_CLOSED_INVOICE")
    Invoice getSuccessfulCreatedInvoice(Invoice invoice);

    @Mapping(target = "invoiceProcessStatus", constant = "FAILED_CLOSED_INVOICE")
    Invoice getFailedCreatedInvoice(Invoice invoice);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
