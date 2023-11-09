package com.carrental.expense.util;

import com.carrental.document.model.Invoice;
import com.carrental.document.model.Revenue;
import com.carrental.dto.InvoiceDto;
import com.carrental.dto.RevenueDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoice(Invoice invoice, InvoiceDto invoiceDto) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceDto.getCarDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceDto.getIsVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Optional.ofNullable(invoiceDto.getDamageCost()).orElseThrow().doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Optional.ofNullable(invoiceDto.getAdditionalPayment()).orElseThrow().doubleValue());
        assertEquals(invoice.getTotalAmount(), Optional.ofNullable(invoiceDto.getTotalAmount()).orElseThrow().doubleValue());
        assertEquals(invoice.getComments(), invoiceDto.getComments());
    }

    public static void assertRevenue(Revenue revenue, RevenueDto revenueDto) {
        assertEquals(revenue.getDateOfRevenue(), revenueDto.getDateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Optional.ofNullable(revenueDto.getAmountFromBooking()).orElseThrow().doubleValue());
    }

}
