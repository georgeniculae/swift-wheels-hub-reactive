package com.autohubreactive.expense.mapper;

import com.autohubreactive.dto.common.BookingResponse;
import com.autohubreactive.dto.invoice.InvoiceReprocessRequest;
import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.expense.util.AssertionUtil;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.model.invoice.Invoice;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class InvoiceMapperTest {

    private final InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse = invoiceMapper.mapEntityToDto(invoice);

        assertNotNull(invoiceResponse);
        AssertionUtil.assertInvoiceResponse(invoice, invoiceResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(invoiceMapper.mapEntityToDto(null));
    }

    @Test
    void getNewInvoiceTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Invoice invoice = invoiceMapper.getNewInvoice(bookingResponse);

        assertNotNull(invoice);
        AssertionUtil.assertInvoiceRequest(invoice, bookingResponse);
    }

    @Test
    void getNewInvoiceTest_null() {
        assertNull(invoiceMapper.getNewInvoice(null));
    }

    @Test
    void mapToInvoiceReprocessRequestTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);
        invoice.setReturnBranchId(new ObjectId("64f361caf291ae086e179547"));

        InvoiceReprocessRequest invoiceReprocessRequest = invoiceMapper.mapToInvoiceReprocessRequest(invoice);

        assertNotNull(invoiceReprocessRequest);
        AssertionUtil.assertInvoiceReprocessRequest(invoice, invoiceReprocessRequest);
    }

    @Test
    void mapToInvoiceReprocessRequestTest_null() {
        assertNull(invoiceMapper.mapToInvoiceReprocessRequest(null));
    }

}
