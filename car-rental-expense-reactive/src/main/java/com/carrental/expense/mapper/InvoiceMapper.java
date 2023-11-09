package com.carrental.expense.mapper;

import com.carrental.document.model.Invoice;
import com.carrental.dto.InvoiceDto;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    InvoiceDto mapEntityToDto(Invoice invoice);

    Invoice mapDtoToEntity(InvoiceDto invoiceDto);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
