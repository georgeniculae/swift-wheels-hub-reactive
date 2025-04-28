package com.autohubreactive.expense.service;

import com.autohubreactive.dto.BookingClosingDetails;
import com.autohubreactive.dto.CarState;
import com.autohubreactive.dto.CarUpdateDetails;
import com.autohubreactive.dto.InvoiceReprocessRequest;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.expense.producer.BookingUpdateProducerService;
import com.autohubreactive.expense.producer.CarStatusUpdateProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceReprocessingService {

    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;

    public Mono<Void> reprocessInvoice(InvoiceReprocessRequest invoiceReprocessRequest) {
        return carStatusUpdateProducerService.sendCarUpdateDetails(getCarUpdateDetails(invoiceReprocessRequest))
                .then(Mono.defer(() -> bookingUpdateProducerService.sendBookingClosingDetails(getBookingClosingDetails(invoiceReprocessRequest))))
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess invoice: {}", e.getMessage());

                    return Mono.error(new AutoHubException(e.getMessage()));
                });
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceReprocessRequest invoiceReprocessRequest) {
        return BookingClosingDetails.builder()
                .bookingId(invoiceReprocessRequest.bookingId())
                .returnBranchId(invoiceReprocessRequest.returnBranchId())
                .build();
    }

    private CarUpdateDetails getCarUpdateDetails(InvoiceReprocessRequest invoiceReprocessRequest) {
        return CarUpdateDetails.builder()
                .carId(invoiceReprocessRequest.carId())
                .carState(invoiceReprocessRequest.isVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .receptionistEmployeeId(invoiceReprocessRequest.receptionistEmployeeId())
                .build();
    }

}
