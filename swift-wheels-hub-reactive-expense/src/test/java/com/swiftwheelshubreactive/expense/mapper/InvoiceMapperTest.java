package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.expense.util.AssertionUtils;
import com.swiftwheelshubreactive.expense.util.TestUtils;
import com.swiftwheelshubreactive.model.Invoice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InvoiceMapperTest {

    private final InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse = invoiceMapper.mapEntityToDto(invoice);

        assertNotNull(invoiceResponse);
        AssertionUtils.assertInvoiceResponse(invoice, invoiceResponse);
    }

    @Test
    void mapDtoToEntityTest_success() {
        InvoiceRequest invoiceRequest =
                TestUtils.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        Invoice invoice = invoiceMapper.mapDtoToEntity(invoiceRequest);

        assertNotNull(invoice);
        AssertionUtils.assertInvoiceRequest(invoice, invoiceRequest);
    }

}
