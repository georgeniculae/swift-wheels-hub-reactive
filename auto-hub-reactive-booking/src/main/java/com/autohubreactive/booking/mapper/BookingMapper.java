package com.autohubreactive.booking.mapper;

import com.autohubreactive.dto.common.AuthenticationInfo;
import com.autohubreactive.dto.common.AvailableCarInfo;
import com.autohubreactive.dto.booking.BookingRequest;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.booking.CreatedBookingReprocessRequest;
import com.autohubreactive.dto.booking.UpdatedBookingReprocessRequest;
import com.autohubreactive.model.booking.Booking;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BookingMapper {

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapEntityToDto(Booking booking);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerUsername", expression = "java(authenticationInfo.username())")
    @Mapping(target = "customerEmail", expression = "java(authenticationInfo.email())")
    @Mapping(target = "actualCarId", expression = "java(mapStringToObjectId(availableCarInfo.id()))")
    @Mapping(target = "dateOfBooking", expression = "java(getDateOfBooking())")
    @Mapping(target = "rentalBranchId", expression = "java(mapStringToObjectId(availableCarInfo.actualBranchId()))")
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "rentalCarPrice", expression = "java(availableCarInfo.amount())")
    Booking getNewBooking(BookingRequest bookingRequest, AvailableCarInfo availableCarInfo, AuthenticationInfo authenticationInfo);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapToBookingResponse(CreatedBookingReprocessRequest createdBookingReprocessRequest);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapToBookingResponse(UpdatedBookingReprocessRequest createdBookingReprocessRequest);

    @Mapping(target = "returnBranchId", source = "returnBranchId")
    @Mapping(target = "status", constant = "CLOSED")
    Booking getClosedBooking(Booking existingBooking, String returnBranchId);

    @Mapping(target = "dateFrom", source = "dateFrom")
    @Mapping(target = "dateTo", source = "dateTo")
    Booking getUpdatedBooking(Booking existingBooking, LocalDate dateFrom, LocalDate dateTo);

    @Mapping(target = "id", expression = "java(existingBooking.getId())")
    @Mapping(target = "dateOfBooking", expression = "java(existingBooking.getDateOfBooking())")
    @Mapping(target = "dateFrom", expression = "java(updatedBookingRequest.dateFrom())")
    @Mapping(target = "dateTo", expression = "java(updatedBookingRequest.dateTo())")
    @Mapping(target = "actualCarId", expression = "java(mapStringToObjectId(availableCarInfo.id()))")
    @Mapping(target = "previousCarId", expression = "java(existingBooking.getActualCarId())")
    @Mapping(target = "rentalCarPrice", expression = "java(availableCarInfo.amount())")
    @Mapping(target = "rentalBranchId", expression = "java(mapStringToObjectId(availableCarInfo.actualBranchId()))")
    Booking getUpdatedBookingWithNewData(Booking existingBooking, BookingRequest updatedBookingRequest, AvailableCarInfo availableCarInfo);

    CreatedBookingReprocessRequest getCreatedBookingReprocessRequest(Booking content);

    UpdatedBookingReprocessRequest getUpdatedBookingReprocessRequest(Booking content, boolean isCarChanged);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

    default LocalDate getDateOfBooking() {
        return LocalDate.now();
    }

}
