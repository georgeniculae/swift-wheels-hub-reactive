package com.autohubreactive.booking.mapper;

import com.autohubreactive.dto.BookingRequest;
import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.CreatedBookingReprocessRequest;
import com.autohubreactive.dto.UpdatedBookingReprocessRequest;
import com.autohubreactive.model.Booking;
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

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapToBookingResponse(CreatedBookingReprocessRequest createdBookingReprocessRequest);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapToBookingResponse(UpdatedBookingReprocessRequest createdBookingReprocessRequest);

    Booking getNewBookingInstance(Booking booking);

    CreatedBookingReprocessRequest getCreatedBookingReprocessRequest(Booking content);

    UpdatedBookingReprocessRequest getUpdatedBookingReprocessRequest(Booking content, boolean isCarChanged);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return ObjectUtils.isEmpty(id) ? null : new ObjectId(id);
    }

}
