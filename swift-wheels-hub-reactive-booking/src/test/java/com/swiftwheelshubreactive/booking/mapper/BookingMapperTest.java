package com.swiftwheelshubreactive.booking.mapper;

import com.swiftwheelshubreactive.booking.util.AssertionUtils;
import com.swiftwheelshubreactive.booking.util.TestUtil;
import com.swiftwheelshubreactive.dto.BookingRequest;
import com.swiftwheelshubreactive.dto.BookingResponse;
import com.swiftwheelshubreactive.model.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mongodb.assertions.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);

        AssertionUtils.assertBookingResponse(booking, bookingResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(bookingMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        Booking actualBooking = bookingMapper.mapDtoToEntity(bookingRequest);

        AssertionUtils.assertBookingRequest(actualBooking, bookingRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(bookingMapper.mapDtoToEntity(null));
    }

}
