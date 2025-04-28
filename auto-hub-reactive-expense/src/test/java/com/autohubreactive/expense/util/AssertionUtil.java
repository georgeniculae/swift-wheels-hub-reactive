package com.autohubreactive.expense.util;

import com.autohubreactive.dto.InvoiceReprocessRequest;
import com.autohubreactive.dto.InvoiceRequest;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.dto.RevenueResponse;
import com.autohubreactive.model.Invoice;
import com.autohubreactive.model.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertInvoiceRequest(Invoice invoice, InvoiceRequest invoiceRequest) {
        assertEquals(invoice.getCarReturnDate(), invoiceRequest.carReturnDate());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceRequest.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceRequest.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceRequest.additionalPayment());
        assertEquals(invoice.getComments(), invoiceRequest.comments());
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
