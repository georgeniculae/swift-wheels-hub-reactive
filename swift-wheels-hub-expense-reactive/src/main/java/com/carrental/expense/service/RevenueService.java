package com.carrental.expense.service;

import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.model.Invoice;
import com.swiftwheelshub.model.Revenue;
import com.carrental.dto.RevenueDto;
import com.carrental.expense.mapper.RevenueMapper;
import com.carrental.expense.model.Outbox;
import com.carrental.expense.repository.InvoiceRepository;
import com.carrental.expense.repository.RevenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class RevenueService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_OF_REVENUE = "dateOfRevenue";
    private final RevenueRepository revenueRepository;
    private final InvoiceRepository invoiceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final OutboxService outboxService;
    private final RevenueMapper revenueMapper;

    public Flux<RevenueDto> findAllRevenues() {
        return revenueRepository.findAll()
                .map(revenueMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all revenues: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<RevenueDto> findRevenuesByDate(String dateOfRevenue) {
        return findByDateOfRevenue(dateOfRevenue)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Revenue from date: " + dateOfRevenue + " does not exist"
                                )
                        )
                )
                .map(revenueMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding revenues by date: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Double> getTotalAmount() {
        return revenueRepository.getTotalAmount()
                .onErrorResume(e -> {
                    log.error("Error while getting total amount: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @Transactional
    public Mono<Invoice> saveInvoiceRevenueAndOutboxTransactional(Invoice invoice) {
        return invoiceRepository.save(invoice)
                .flatMap(savedInvoice -> outboxService.saveOutbox(invoice, Outbox.Operation.CLOSE))
                .flatMap(savedOutbox -> revenueRepository.save(getRevenue(savedOutbox.getContent()))
                        .map(revenue -> savedOutbox.getContent()))
                .onErrorResume(e -> {
                    log.error("Error during transactional saving of outbox and revenue: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    private Revenue getRevenue(Invoice invoice) {
        Revenue revenue = new Revenue();

        revenue.setDateOfRevenue(invoice.getCarDateOfReturn());
        revenue.setAmountFromBooking(invoice.getTotalAmount());

        return revenue;
    }

    private Flux<Revenue> findByDateOfRevenue(String dateOfRevenue) {
        return reactiveMongoTemplate.find(getDateOfRevenueQuery(dateOfRevenue), Revenue.class);
    }

    private Query getDateOfRevenueQuery(String dateOfRevenue) {
        Date dateOfBookingAsDate;
        Date dayAfterDateOfBookingAsDate;

        try {
            dateOfBookingAsDate = new SimpleDateFormat(DATE_FORMAT).parse(dateOfRevenue);

            dayAfterDateOfBookingAsDate = new SimpleDateFormat(DATE_FORMAT)
                    .parse(LocalDate.parse(dateOfRevenue)
                            .plusDays(1)
                            .format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        } catch (ParseException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }

        Criteria dateOfBookingCriteria = Criteria.where(DATE_OF_REVENUE)
                .gte(dateOfBookingAsDate)
                .lt(dayAfterDateOfBookingAsDate);

        return new Query().addCriteria(dateOfBookingCriteria);
    }

}
