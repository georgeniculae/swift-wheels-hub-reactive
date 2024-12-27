package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceRequest;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InvoiceReprocessingService {

    private final BookingUpdateProducerService bookingUpdateProducerService;
    private final CarStatusUpdateProducerService carStatusUpdateProducerService;
    private final BookingRollbackProducerService bookingRollbackProducerService;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;
    private final RetryHandler retryHandler;

    public Mono<Void> reprocessInvoice(InvoiceRequest invoiceRequest) {
        return bookingUpdateProducerService.sendBookingClosingDetails(getBookingClosingDetails(invoiceRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> processCarStatusUpdate(invoiceRequest))
                .flatMap(_ -> invoiceRepository.save(invoiceMapper.mapRequestToSuccessfulInvoice(invoiceRequest)))
                .retryWhen(retryHandler.retry())
                .then();
    }

    private Mono<Boolean> processCarStatusUpdate(InvoiceRequest invoiceRequest) {
        return carStatusUpdateProducerService.sendCarUpdateDetails(getCarUpdateDetails(invoiceRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> bookingRollbackProducerService.sendBookingId(invoiceRequest.bookingId()));
    }

    private BookingClosingDetails getBookingClosingDetails(InvoiceRequest invoiceRequest) {
        return BookingClosingDetails.builder()
                .bookingId(invoiceRequest.bookingId())
                .returnBranchId(invoiceRequest.returnBranchId())
                .build();
    }

    private CarUpdateDetails getCarUpdateDetails(InvoiceRequest invoiceRequest) {
        return CarUpdateDetails.builder()
                .carId(invoiceRequest.carId())
                .carState(invoiceRequest.isVehicleDamaged() ? CarState.BROKEN : CarState.AVAILABLE)
                .receptionistEmployeeId(invoiceRequest.receptionistEmployeeId())
                .build();
    }

}
