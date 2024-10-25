package com.swiftwheelshubreactive.expense.scheduler;

import com.swiftwheelshubreactive.dto.AuthenticationInfo;
import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.BookingUpdateResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.StatusUpdateResponse;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.expense.service.BookingService;
import com.swiftwheelshubreactive.expense.service.CarService;
import com.swiftwheelshubreactive.expense.service.RevenueService;
import com.swiftwheelshubreactive.model.Invoice;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FailedInvoiceScheduler {

    private final CarService carService;
    private final RevenueService revenueService;
    private final InvoiceRepository invoiceRepository;
    private final BookingService bookingService;
    private final InvoiceMapper invoiceMapper;

    @Value("${apikey.secret}")
    private String apikey;

    @Value("${apikey.machine-role}")
    private String machineRole;

    @Scheduled(fixedDelay = 5000)
    public void processFailedInvoices() {
        invoiceRepository.findAllFailedInvoices()
                .onBackpressureBuffer()
                .flatMap(this::processFailedOutbox)
                .subscribe();
    }

    private Mono<Invoice> processFailedOutbox(Invoice invoice) {
        return updateProcesses(getAuthenticationInfo(), invoice)
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> revenueService.processInvoiceCreation(invoiceMapper.getSuccessfulCreatedInvoice(invoice)));
    }

    private Mono<Boolean> updateProcesses(AuthenticationInfo authenticationInfo, Invoice invoice) {
        return Mono.zip(
                carService.setCarAsAvailable(authenticationInfo, getCarUpdateDetails(invoice), 5),
                bookingService.closeBooking(authenticationInfo, getBookingClosingDetails(invoice), 0),
                this::getUpdateResponses
        );
    }

    private AuthenticationInfo getAuthenticationInfo() {
        return AuthenticationInfo.builder()
                .apikey(apikey)
                .roles(getRoles())
                .build();
    }

    private List<String> getRoles() {
        return List.of(machineRole);
    }

    private CarUpdateDetails getCarUpdateDetails(Invoice invoiceRequest) {
        return CarUpdateDetails.builder()
                .carId(invoiceRequest.getCarId().toString())
                .receptionistEmployeeId(invoiceRequest.getReceptionistEmployeeId().toString())
                .carState(invoiceRequest.getIsVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .build();
    }

    private boolean getUpdateResponses(StatusUpdateResponse statusUpdateResponse, BookingUpdateResponse bookingUpdateResponse) {
        return statusUpdateResponse.isUpdateSuccessful() && bookingUpdateResponse.isSuccessful();
    }

    private BookingClosingDetails getBookingClosingDetails(Invoice invoice) {
        return BookingClosingDetails.builder()
                .bookingId(invoice.getBookingId().toString())
                .returnBranchId(invoice.getReturnBranchId().toString())
                .build();
    }

}
