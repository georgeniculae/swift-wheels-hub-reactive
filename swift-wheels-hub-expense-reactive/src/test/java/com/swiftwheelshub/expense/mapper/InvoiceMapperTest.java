package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.expense.util.AssertionUtils;
import com.swiftwheelshub.expense.util.TestUtils;
import com.swiftwheelshub.model.Invoice;
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
                TestUtils.getResourceAsJson("/data/InvoiceResponse.json", InvoiceRequest.class);

        Invoice invoice = invoiceMapper.mapDtoToEntity(invoiceRequest);

        assertNotNull(invoice);
        AssertionUtils.assertInvoiceRequest(invoice, invoiceRequest);
    }

}
