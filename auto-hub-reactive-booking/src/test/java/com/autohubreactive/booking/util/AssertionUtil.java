package com.autohubreactive.booking.util;

import com.autohubreactive.dto.booking.BookingRequest;
import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.model.booking.Booking;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertBookingRequest(Booking booking, BookingRequest bookingRequest) {
        assertEquals(booking.getDateFrom(), bookingRequest.dateFrom());
        assertEquals(booking.getDateTo(), bookingRequest.dateTo());
    }

    public static void assertBookingResponse(Booking booking, BookingResponse bookingResponse) {
        assertEquals(booking.getDateOfBooking(), bookingResponse.dateOfBooking());
        assertEquals(booking.getDateFrom(), bookingResponse.dateFrom());
        assertEquals(booking.getDateTo(), bookingResponse.dateTo());
    }

}
