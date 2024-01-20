package com.carrental.booking.mapper;

import com.carrental.booking.util.AssertionUtils;
import com.carrental.booking.util.TestUtils;
import com.swiftwheelshub.model.Booking;
import com.carrental.dto.BookingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtils.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingDto bookingDto = bookingMapper.mapEntityToDto(booking);

        AssertionUtils.assertBooking(booking, bookingDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        Booking actualBooking = bookingMapper.mapDtoToEntity(bookingDto);

        AssertionUtils.assertBooking(actualBooking, bookingDto);
    }

}
