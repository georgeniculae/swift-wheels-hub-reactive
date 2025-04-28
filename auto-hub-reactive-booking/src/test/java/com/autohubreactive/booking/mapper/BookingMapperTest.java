package com.autohubreactive.booking.mapper;

import com.autohubreactive.booking.util.AssertionUtil;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.BookingRequest;
import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.model.Booking;
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

        AssertionUtil.assertBookingResponse(booking, bookingResponse);
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

        AssertionUtil.assertBookingRequest(actualBooking, bookingRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(bookingMapper.mapDtoToEntity(null));
    }

}
