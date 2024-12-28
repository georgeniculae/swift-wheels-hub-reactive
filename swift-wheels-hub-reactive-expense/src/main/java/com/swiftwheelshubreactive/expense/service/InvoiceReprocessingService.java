package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.producer.BookingRollbackProducerService;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.lib.retry.RetryHandler;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
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

    public Mono<Void> reprocessInvoice(InvoiceReprocessRequest invoiceReprocessRequest) {
        return bookingUpdateProducerService.sendBookingClosingDetails(getBookingClosingDetails(invoiceReprocessRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> processCarStatusUpdate(invoiceReprocessRequest))
                .flatMap(_ -> markInvoiceAsSuccessful(invoiceReprocessRequest))
                .retryWhen(retryHandler.retry());
    }

    private Mono<Boolean> processCarStatusUpdate(InvoiceReprocessRequest invoiceReprocessRequest) {
        return carStatusUpdateProducerService.sendCarUpdateDetails(getCarUpdateDetails(invoiceReprocessRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> bookingRollbackProducerService.sendBookingId(invoiceReprocessRequest.bookingId()));
    }

    private Mono<Void> markInvoiceAsSuccessful(InvoiceReprocessRequest invoiceReprocessRequest) {
        return invoiceRepository.findById(MongoUtil.getObjectId(invoiceReprocessRequest.invoiceId()))
                .map(invoiceMapper::getSuccessfulCreatedInvoice)
                .flatMap(invoiceRepository::save)
                .then();
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
