package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingUpdateResponse;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapperImpl;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.expense.service.BookingService;
import com.swiftwheelshubreactive.expense.service.CarService;
import com.swiftwheelshubreactive.expense.service.RevenueService;
import com.swiftwheelshubreactive.expense.util.TestUtil;
import com.swiftwheelshubreactive.model.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FailedInvoiceSchedulerTest {

    @InjectMocks
    private FailedInvoiceScheduler failedInvoiceScheduler;

    @Mock
    private CarService carService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private BookingService bookingService;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(failedInvoiceScheduler, "apikey", "apikey");
        ReflectionTestUtils.setField(failedInvoiceScheduler, "machineRole", "invoice_service");
    }

    @Test
    void processFailedInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/FailedClosedInvoice.json", Invoice.class);

        BookingUpdateResponse bookingUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulBookingUpdateResponse.json", BookingUpdateResponse.class);

        StatusUpdateResponse statusUpdateResponse =
                TestUtil.getResourceAsJson("/data/SuccessfulStatusUpdateResponse.json", StatusUpdateResponse.class);

        when(invoiceRepository.findAllFailedInvoices()).thenReturn(Flux.just(invoice));
        when(carService.setCarAsAvailable(any(AuthenticationInfo.class), any(CarUpdateDetails.class), anyInt()))
                .thenReturn(Mono.just(statusUpdateResponse));
        when(bookingService.closeBooking(any(AuthenticationInfo.class), any(BookingClosingDetails.class), anyInt()))
                .thenReturn(Mono.just(bookingUpdateResponse));
        when(revenueService.processClosing(any(Invoice.class))).thenReturn(Mono.just(invoice));

        assertDoesNotThrow(() -> failedInvoiceScheduler.processFailedInvoices());

        verify(invoiceMapper).getSuccessfulCreatedInvoice(any(Invoice.class));
    }

}
