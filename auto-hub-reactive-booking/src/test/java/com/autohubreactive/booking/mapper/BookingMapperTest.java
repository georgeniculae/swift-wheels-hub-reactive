package com.autohubreactive.booking.mapper;

import com.autohubreactive.booking.util.AssertionUtil;
import com.autohubreactive.booking.util.TestUtil;
import com.autohubreactive.dto.booking.BookingRequest;
import com.autohubreactive.dto.common.AuthenticationInfo;
import com.autohubreactive.dto.common.AvailableCarInfo;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.model.booking.Booking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
    void getNewBookingTest_success() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey("apikey")
                .username("user")
                .email("user@mail.com")
                .roles(List.of("admin"))
                .build();

        Booking actualBooking = bookingMapper.getNewBooking(bookingRequest, availableCarInfo, authenticationInfo);

        AssertionUtil.assertBookingRequest(actualBooking, bookingRequest);
    }

    @Test
    void getNewBookingTest_null() {
        assertNull(bookingMapper.getNewBooking(null, null, null));
    }

}
