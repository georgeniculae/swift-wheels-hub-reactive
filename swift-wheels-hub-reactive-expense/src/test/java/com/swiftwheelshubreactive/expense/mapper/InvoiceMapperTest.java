package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.util.AssertionUtil;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Invoice;
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
    void mapDtoToEntityTest_success() {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        Invoice invoice = invoiceMapper.mapDtoToEntity(invoiceRequest);

        assertNotNull(invoice);
        AssertionUtil.assertInvoiceRequest(invoice, invoiceRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(invoiceMapper.mapDtoToEntity(null));
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
