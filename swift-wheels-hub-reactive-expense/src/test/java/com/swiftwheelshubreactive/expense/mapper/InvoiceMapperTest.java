package com.swiftwheelshubreactive.expense.mapper;

import com.swiftwheelshubreactive.dto.InvoiceProcessState;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.util.AssertionUtil;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Invoice;
import com.swiftwheelshubreactive.model.InvoiceProcessStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void mapDtoToEntityTest_success() {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        Invoice invoice = invoiceMapper.mapDtoToEntity(invoiceRequest);

        assertNotNull(invoice);
        AssertionUtil.assertInvoiceRequest(invoice, invoiceRequest);
    }

    @Test
    void mapToInvoiceProcessStateTest_null() {
        assertNull(invoiceMapper.mapToInvoiceProcessState(null));
    }

    @Test
    void mapToInvoiceProcessStateTest_savedClosedInvoice() {
        InvoiceProcessState invoiceProcessState =
                invoiceMapper.mapToInvoiceProcessState(InvoiceProcessStatus.SAVED_CLOSED_INVOICE);

        assertEquals(InvoiceProcessState.SAVED_CLOSED_INVOICE.name(), invoiceProcessState.name());
    }

    @Test
    void mapToInvoiceProcessStateTest_failedClosedInvoice() {
        InvoiceProcessState invoiceProcessState =
                invoiceMapper.mapToInvoiceProcessState(InvoiceProcessStatus.FAILED_CLOSED_INVOICE);

        assertEquals(InvoiceProcessState.FAILED_CLOSED_INVOICE.name(), invoiceProcessState.name());
    }

    @Test
    void mapToInvoiceProcessStateTest_defaultCase() {
        SwiftWheelsHubException swiftWheelsHubException = assertThrows(
                SwiftWheelsHubException.class,
                () -> invoiceMapper.mapToInvoiceProcessState(InvoiceProcessStatus.IN_CLOSING)
        );

        assertEquals("Invalid invoice process status: IN_CLOSING", swiftWheelsHubException.getMessage());
    }

}
