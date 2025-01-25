package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.BookingClosingDetails;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.InvoiceReprocessRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.expense.mapper.InvoiceMapper;
import com.swiftwheelshubreactive.expense.producer.BookingUpdateProducerService;
import com.swiftwheelshubreactive.expense.producer.CarStatusUpdateProducerService;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.Invoice;
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
    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public Mono<Void> reprocessInvoice(InvoiceReprocessRequest invoiceReprocessRequest) {
        return carStatusUpdateProducerService.sendCarUpdateDetails(getCarUpdateDetails(invoiceReprocessRequest))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> bookingUpdateProducerService.sendBookingClosingDetails(getBookingClosingDetails(invoiceReprocessRequest)))
                .filter(Boolean.TRUE::equals)
                .flatMap(_ -> markInvoiceAsSuccessful(invoiceReprocessRequest))
                .switchIfEmpty(Mono.error(new SwiftWheelsHubException("Invoice reprocessing failed")))
                .then()
                .onErrorResume(e -> {
                    log.error("Error while trying to reprocess invoice: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    private Mono<Invoice> markInvoiceAsSuccessful(InvoiceReprocessRequest invoiceReprocessRequest) {
        return invoiceRepository.findById(MongoUtil.getObjectId(invoiceReprocessRequest.invoiceId()))
                .map(invoiceMapper::getSuccessfulCreatedInvoice)
                .flatMap(invoiceRepository::save);
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
