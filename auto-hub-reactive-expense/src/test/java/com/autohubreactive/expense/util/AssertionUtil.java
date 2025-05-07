package com.autohubreactive.expense.util;

import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoice.InvoiceReprocessRequest;
import com.autohubreactive.dto.invoice.RevenueResponse;
import com.autohubreactive.model.invoice.Invoice;
import com.autohubreactive.model.invoice.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertInvoiceRequest(Invoice invoice, BookingResponse bookingResponse) {
        assertEquals(bookingResponse.customerUsername(), invoice.getCustomerUsername());
        assertEquals(bookingResponse.customerEmail(), invoice.getCustomerEmail());
        assertEquals(bookingResponse.carId(), invoice.getCarId().toString());
        assertEquals(bookingResponse.returnBranchId(), invoice.getReturnBranchId().toString());
        assertEquals(bookingResponse.dateFrom(), invoice.getDateFrom());
        assertEquals(bookingResponse.dateTo(), invoice.getDateTo());
    }

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarReturnDate(), invoiceResponse.carReturnDate());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceResponse.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceResponse.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceResponse.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceResponse.totalAmount());
        assertEquals(invoice.getComments(), invoiceResponse.comments());
    }

    public static void assertRevenueResponse(Revenue revenue, RevenueResponse revenueResponse) {
        assertEquals(revenue.getDateOfRevenue(), revenueResponse.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), revenueResponse.amountFromBooking());
    }

    public static void assertInvoiceReprocessRequest(Invoice invoice, InvoiceReprocessRequest invoiceReprocessRequest) {
        assertEquals(invoice.getCarReturnDate(), invoiceReprocessRequest.carReturnDate());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceReprocessRequest.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceReprocessRequest.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceReprocessRequest.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceReprocessRequest.totalAmount());
        assertEquals(invoice.getComments(), invoiceReprocessRequest.comments());
    }
}
