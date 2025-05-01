package com.autohubreactive.expense.mapper;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.InvoiceReprocessRequest;
import com.autohubreactive.dto.InvoiceRequest;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.model.Invoice;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface InvoiceMapper {

    InvoiceResponse mapEntityToDto(Invoice invoice);

    Invoice mapDtoToEntity(InvoiceRequest invoiceRequest);

    @Mapping(target = "id", expression = "java(invoice.getId())")
    @Mapping(target = "customerUsername", expression = "java(invoice.getCustomerUsername())")
    @Mapping(target = "customerEmail", expression = "java(invoice.getCustomerEmail())")
    @Mapping(target = "returnBranchId", expression = "java(mapStringToObjectId(newBookingResponse.returnBranchId()))")
    @Mapping(target = "dateTo", expression = "java(invoice.getDateTo())")
    @Mapping(target = "dateFrom", expression = "java(invoice.getDateFrom())")
    @Mapping(target = "bookingId", expression = "java(mapStringToObjectId(newBookingResponse.id()))")
    @Mapping(target = "additionalPayment", expression = "java(invoice.getAdditionalPayment())")
    @Mapping(target = "comments", expression = "java(invoice.getComments())")
    @Mapping(target = "carReturnDate", expression = "java(invoice.getCarReturnDate())")
    @Mapping(target = "isVehicleDamaged", expression = "java(invoice.getIsVehicleDamaged())")
    @Mapping(target = "carId", expression = "java(mapStringToObjectId(newBookingResponse.carId()))")
    @Mapping(target = "rentalCarPrice", expression = "java(newBookingResponse.rentalCarPrice())")
    Invoice getInvoiceAfterBookingUpdate(Invoice invoice, BookingResponse newBookingResponse);

    @Mapping(target = "damageCost", expression = "java(getDamageCost(invoiceRequest))")
    @Mapping(target = "totalAmount", expression = "java(getTotalAmount(invoiceRequest, existingInvoice))")
    @Mapping(target = "carReturnDate", expression = "java(invoiceRequest.carReturnDate())")
    @Mapping(target = "receptionistEmployeeId", expression = "java(mapStringToObjectId(invoiceRequest.receptionistEmployeeId()))")
    @Mapping(target = "returnBranchId", expression = "java(mapStringToObjectId(invoiceRequest.returnBranchId()))")
    @Mapping(target = "bookingId", expression = "java(mapStringToObjectId(invoiceRequest.bookingId()))")
    @Mapping(target = "isVehicleDamaged", expression = "java(invoiceRequest.isVehicleDamaged())")
    @Mapping(target = "additionalPayment", expression = "java(getAdditionalPayment(invoiceRequest))")
    @Mapping(target = "comments", expression = "java(invoiceRequest.comments())")
    Invoice getUpdatedInstance(Invoice existingInvoice, InvoiceRequest invoiceRequest);

    InvoiceReprocessRequest mapToInvoiceReprocessRequest(Invoice invoice);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

    default BigDecimal getTotalAmount(InvoiceRequest invoiceRequest, Invoice existingInvoice) {
        LocalDate carReturnDate = invoiceRequest.carReturnDate();
        LocalDate bookingDateTo = existingInvoice.getDateTo();
        LocalDate bookingDateFrom = existingInvoice.getDateFrom();
        BigDecimal rentalCarPrice = existingInvoice.getRentalCarPrice();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getMoneyForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, rentalCarPrice);
        }

        return rentalCarPrice.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(getDamageCost(invoiceRequest));
    }

    default BigDecimal getDamageCost(InvoiceRequest invoiceRequest) {
        BigDecimal damageCost = invoiceRequest.damageCost();

        return ObjectUtils.isEmpty(invoiceRequest) ? BigDecimal.ZERO : damageCost;
    }

    default BigDecimal getAdditionalPayment(InvoiceRequest invoiceRequest) {
        BigDecimal additionalPayment = invoiceRequest.additionalPayment();

        return ObjectUtils.isEmpty(additionalPayment) ? BigDecimal.ZERO : additionalPayment;
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private BigDecimal getMoneyForLateReturn(LocalDate carReturnDate,
                                             LocalDate bookingDateTo,
                                             LocalDate bookingDateFrom,
                                             BigDecimal carAmount) {
        return carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateFrom, bookingDateTo)))
                .add(carAmount.multiply(BigDecimal.valueOf(getDaysPeriod(bookingDateTo, carReturnDate) * 2L)));
    }

}
