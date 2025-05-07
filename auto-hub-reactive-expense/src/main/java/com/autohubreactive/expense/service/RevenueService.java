package com.autohubreactive.expense.service;

import com.autohubreactive.dto.invoice.RevenueResponse;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.expense.mapper.RevenueMapper;
import com.autohubreactive.expense.model.Outbox;
import com.autohubreactive.expense.repository.InvoiceRepository;
import com.autohubreactive.expense.repository.RevenueRepository;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.model.invoice.Invoice;
import com.autohubreactive.model.invoice.Revenue;
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

                    return new AutoHubException(e.getMessage());
                });
    }

    public Flux<RevenueResponse> findRevenuesByDate(String dateOfRevenue) {
        return findByDateOfRevenue(dateOfRevenue)
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Revenue from date: " + dateOfRevenue + " does not exist")))
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

                    return new AutoHubException(e.getMessage());
                });
    }

    @Transactional
    public Mono<Invoice> processClosing(Invoice invoice) {
        return invoiceRepository.save(invoice)
                .flatMap(outboxService::saveOutbox)
                .delayUntil(savedOutbox -> revenueRepository.save(getRevenue(savedOutbox.getContent())))
                .map(Outbox::getContent)
                .onErrorMap(e -> {
                    log.error("Error during transactional saving of outbox and revenue: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Revenue getRevenue(Invoice invoice) {
        Revenue revenue = new Revenue();

        revenue.setDateOfRevenue(invoice.getCarReturnDate());
        revenue.setAmountFromBooking(invoice.getTotalAmount());

        return revenue;
    }

    private Flux<Revenue> findByDateOfRevenue(String dateOfRevenue) {
        return reactiveMongoTemplate.find(getDateOfRevenueQuery(dateOfRevenue), Revenue.class);
    }

    private Query getDateOfRevenueQuery(String dateOfRevenue) {
        Date dateOfBookingAsDate = formatDate(dateOfRevenue);

        String format = LocalDate.parse(dateOfRevenue)
                .plusDays(1)
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        Date dayAfterDateOfBookingAsDate = formatDate(format);

        Criteria dateOfBookingCriteria = Criteria.where(DATE_OF_REVENUE)
                .gte(dateOfBookingAsDate)
                .lt(dayAfterDateOfBookingAsDate);

        return new Query().addCriteria(dateOfBookingCriteria);
    }

    private Date formatDate(String dateOfRevenue) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(dateOfRevenue);
        } catch (ParseException e) {
            throw new AutoHubException(e.getMessage());
        }
    }

}
