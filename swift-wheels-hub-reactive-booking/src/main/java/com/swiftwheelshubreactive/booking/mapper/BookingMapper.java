package com.swiftwheelshubreactive.booking.mapper;

import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.model.Booking;
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
public interface BookingMapper {

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapEntityToDto(Booking booking);

    @Mapping(target = "actualCarId", source = "carId")
    Booking mapDtoToEntity(BookingRequest bookingRequest);

    Booking getNewBookingInstance(Booking booking);

    @Mapping(target = "bookingProcessStatus", constant = "SAVED_CREATED_BOOKING")
    Booking getSuccessfulCreatedBooking(Booking booking);

    @Mapping(target = "bookingProcessStatus", constant = "SAVED_UPDATED_BOOKING")
    Booking getSuccessfulUpdatedBooking(Booking booking);

    @Mapping(target = "bookingProcessStatus", constant = "FAILED_CREATED_BOOKING")
    Booking getFailedCreatedBooking(Booking pendingBooking);

    @Mapping(target = "bookingProcessStatus", constant = "FAILED_UPDATED_BOOKING")
    Booking getFailedUpdatedBooking(Booking pendingBooking);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
