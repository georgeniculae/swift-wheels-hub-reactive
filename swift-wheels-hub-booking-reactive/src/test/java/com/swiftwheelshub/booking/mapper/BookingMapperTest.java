package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.booking.util.AssertionUtils;
import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.model.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.mongodb.assertions.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

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
                TestUtils.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        Booking actualBooking = bookingMapper.mapDtoToEntity(bookingRequest);

        AssertionUtils.assertBookingRequest(actualBooking, bookingRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(bookingMapper.mapDtoToEntity(null));
    }

}
