package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.InvoiceProcessState;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.model.Invoice;
import com.swiftwheelshubreactive.model.InvoiceProcessStatus;
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

    @Mapping(target = "invoiceProcessState", expression = "java(mapToInvoiceProcessState(invoice.getInvoiceProcessStatus()))")
    InvoiceResponse mapEntityToDto(Invoice invoice);

    Invoice mapDtoToEntity(InvoiceRequest invoiceRequest);

    Invoice getNewInvoiceInstance(Invoice existingInvoice);

    @Mapping(target = "invoiceProcessStatus", constant = "SAVED_CLOSED_INVOICE")
    Invoice getSuccessfulCreatedInvoice(Invoice invoice);

    @Mapping(target = "invoiceProcessStatus", constant = "FAILED_CLOSED_INVOICE")
    Invoice getFailedCreatedInvoice(Invoice invoice);

    @Mapping(target = "invoiceId", expression = "java(id)")
    InvoiceReprocessRequest mapToInvoiceReprocessRequest(String id, InvoiceRequest invoiceRequest);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

    default InvoiceProcessState mapToInvoiceProcessState(InvoiceProcessStatus invoiceProcessStatus) {
        if (ObjectUtils.isEmpty(invoiceProcessStatus)) {
            return null;
        }

        return switch (invoiceProcessStatus) {
            case IN_PROGRESS -> InvoiceProcessState.IN_PROGRESS;
            case SAVED_CLOSED_INVOICE -> InvoiceProcessState.SAVED_CLOSED_INVOICE;
            case FAILED_CLOSED_INVOICE -> InvoiceProcessState.FAILED_CLOSED_INVOICE;
            default -> throw new SwiftWheelsHubException("Invalid invoice process status: " + invoiceProcessStatus);
        };
    }

}
