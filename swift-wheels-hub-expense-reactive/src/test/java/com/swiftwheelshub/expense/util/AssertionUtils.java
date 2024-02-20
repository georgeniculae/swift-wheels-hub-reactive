package com.swiftwheelshub.expense.util;

import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.model.Invoice;
import com.swiftwheelshub.model.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoiceRequest(Invoice invoice, InvoiceRequest invoiceRequest) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceRequest.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceRequest.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceRequest.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceRequest.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceRequest.totalAmount());
        assertEquals(invoice.getComments(), invoiceRequest.comments());
    }

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceResponse.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceResponse.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceResponse.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceResponse.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceResponse.totalAmount());
        assertEquals(invoice.getComments(), invoiceResponse.comments());
    }

    public static void assertRevenueRequest(Revenue revenue, RevenueRequest revenueRequest) {
        assertEquals(revenue.getDateOfRevenue(), revenueRequest.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), revenueRequest.amountFromBooking());
    }

    public static void assertRevenueResponse(Revenue revenue, RevenueResponse revenueResponse) {
        assertEquals(revenue.getDateOfRevenue(), revenueResponse.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), revenueResponse.amountFromBooking());
    }

}
