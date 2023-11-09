package com.carrental.booking.util;

import com.carrental.document.model.Booking;
import com.carrental.dto.BookingDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertBooking(Booking booking, BookingDto bookingDto) {
        assertEquals(booking.getDateOfBooking(), bookingDto.getDateOfBooking());
        assertEquals(booking.getDateFrom(), bookingDto.getDateFrom());
        assertEquals(booking.getDateTo(), bookingDto.getDateTo());
        assertEquals(booking.getAmount(), Optional.ofNullable(bookingDto.getAmount()).orElseThrow().doubleValue());
    }

}
