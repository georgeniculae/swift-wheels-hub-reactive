package com.swiftwheelshubreactive.expense.service;

import com.swiftwheelshubreactive.dto.RevenueResponse;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.expense.mapper.RevenueMapper;
import com.swiftwheelshubreactive.expense.model.Outbox;
import com.swiftwheelshubreactive.expense.repository.InvoiceRepository;
import com.swiftwheelshubreactive.expense.repository.RevenueRepository;
import com.swiftwheelshubreactive.model.Invoice;
import com.swiftwheelshubreactive.model.Revenue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
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

    public Flux<RevenueResponse> findAllRevenues() {
        return revenueRepository.findAll()
                .map(revenueMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all revenues: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<RevenueResponse> findRevenuesByDate(String dateOfRevenue) {
        return findByDateOfRevenue(dateOfRevenue)
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Revenue from date: " + dateOfRevenue + " does not exist")))
                .map(revenueMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding revenues by date: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<BigDecimal> getTotalAmount() {
        return revenueRepository.getTotalAmount()
                .map(BigDecimal::valueOf)
                .onErrorMap(e -> {
                    log.error("Error while getting total amount: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    @Transactional
    public Mono<Invoice> saveInvoiceRevenueAndOutbox(Invoice invoice) {
        return invoiceRepository.save(invoice)
                .flatMap(savedInvoice -> outboxService.saveOutbox(invoice, Outbox.Operation.CLOSE))
                .delayUntil(savedOutbox -> revenueRepository.save(getRevenue(savedOutbox.getContent())))
                .map(Outbox::getContent)
                .onErrorMap(e -> {
                    log.error("Error during transactional saving of outbox and revenue: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
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
                    .parse(
                            LocalDate.parse(dateOfRevenue)
                                    .plusDays(1)
                                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT))
                    );
        } catch (ParseException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }

        Criteria dateOfBookingCriteria = Criteria.where(DATE_OF_REVENUE)
                .gte(dateOfBookingAsDate)
                .lt(dayAfterDateOfBookingAsDate);

        return new Query().addCriteria(dateOfBookingCriteria);
    }

}
