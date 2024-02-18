package com.swiftwheelshub.expense.util;

import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.model.Invoice;
import com.swiftwheelshub.model.Revenue;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoiceRequest(Invoice invoice, InvoiceRequest invoiceRequest) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceRequest.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceRequest.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Optional.ofNullable(invoiceRequest.damageCost()).orElseThrow().doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Optional.ofNullable(invoiceRequest.additionalPayment()).orElseThrow().doubleValue());
        assertEquals(invoice.getTotalAmount(), Optional.ofNullable(invoiceRequest.totalAmount()).orElseThrow().doubleValue());
        assertEquals(invoice.getComments(), invoiceRequest.comments());
    }

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceResponse.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceResponse.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Optional.ofNullable(invoiceResponse.damageCost()).orElseThrow().doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Optional.ofNullable(invoiceResponse.additionalPayment()).orElseThrow().doubleValue());
        assertEquals(invoice.getTotalAmount(), Optional.ofNullable(invoiceResponse.totalAmount()).orElseThrow().doubleValue());
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
