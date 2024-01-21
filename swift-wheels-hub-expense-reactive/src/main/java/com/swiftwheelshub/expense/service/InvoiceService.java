package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetailsDto;
import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.dto.CarStatusEnum;
import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.lib.aspect.LogActivity;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubException;
import com.swiftwheelshub.lib.exceptionhandling.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.MongoUtil;
import com.swiftwheelshub.model.Invoice;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService {

    private static final String TOTAL_AMOUNT = "totalAmount";
    private final InvoiceRepository invoiceRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final RevenueService revenueService;
    private final BookingService bookingService;
    private final InvoiceMapper invoiceMapper;

    public Flux<InvoiceDto> findAllInvoices() {
        return invoiceRepository.findAll()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceDto> findAllActiveInvoices() {
        return findActiveInvoices()
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all active invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceDto> findAllInvoicesByCustomerUsername(String customerUsername) {
        return invoiceRepository.findByCustomerUsername(customerUsername)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoices by customer id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<InvoiceDto> findInvoiceById(String id) {
        return findEntityById(id)
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoice by id: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Flux<InvoiceDto> findInvoicesByComments(String comments) {
        return invoiceRepository.findByComments(comments)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Invoice with comment: " + comments + " does not exist"
                                )
                        )
                )
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding invoices by comments: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countInvoices() {
        return invoiceRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting all invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<Long> countAllActiveInvoices() {
        return countActiveInvoices()
                .onErrorResume(e -> {
                    log.error("Error while counting all active invoices: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    public Mono<InvoiceDto> saveInvoice(BookingDto newBookingDto) {
        return invoiceRepository.existsByBookingId(MongoUtil.getObjectId(newBookingDto.getId()))
                .filter(Boolean.FALSE::equals)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invoice already exists"
                                )
                        )
                )
                .flatMap(existsByBookingId -> invoiceRepository.save(getInvoice(newBookingDto)))
                .map(invoiceMapper::mapEntityToDto);
    }

    public Mono<InvoiceDto> updateInvoiceAfterBookingUpdate(BookingDto newBookingDto) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(newBookingDto.getId()))
                .flatMap(invoice -> {
                    invoice.setCarId(MongoUtil.getObjectId(newBookingDto.getCarId()));

                    return invoiceRepository.save(invoice);
                })
                .map(invoiceMapper::mapEntityToDto);
    }

    @LogActivity(
            sentParameters = {"id", "invoiceDto"},
            activityDescription = "Invoice closing"
    )
    public Mono<InvoiceDto> closeInvoice(String apiKeyToken, String id, InvoiceDto invoiceDto) {
        return validateInvoice(invoiceDto)
                .flatMap(dto -> findEntityById(id))
                .zipWith(bookingService.findBookingById(apiKeyToken, invoiceDto.getBookingId()))
                .map(existingInvoiceBookingDto -> updateInvoiceWithBookingDetails(invoiceDto, existingInvoiceBookingDto))
                .flatMap(revenueService::saveInvoiceRevenueAndOutboxTransactional)
                .delayUntil(invoice -> bookingService.closeBooking(apiKeyToken, getBookingClosingDetailsDto(invoice)))
                .map(invoiceMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while closing invoice: {}", e.getMessage());

                    return Mono.error(new SwiftWheelsHubException(e.getMessage()));
                });
    }

    @Transactional
    public Mono<Void> deleteInvoiceByBookingId(String bookingId) {
        return invoiceRepository.findByBookingId(MongoUtil.getObjectId(bookingId))
                .filter(invoice -> ObjectUtils.isEmpty(invoice.getTotalAmount()))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Invoice cannot be deleted if booking is in progress"
                                )
                        )
                )
                .flatMap(invoice -> invoiceRepository.deleteById(invoice.getId()));
    }

    private Invoice getInvoice(BookingDto newBookingDto) {
        Invoice invoice = new Invoice();

        invoice.setCustomerUsername(newBookingDto.getCustomerUsername());
        invoice.setCustomerEmail(newBookingDto.getCustomerEmail());
        invoice.setCarId(MongoUtil.getObjectId(newBookingDto.getCarId()));
        invoice.setBookingId(MongoUtil.getObjectId(newBookingDto.getId()));

        return invoice;
    }

    private Mono<InvoiceDto> validateInvoice(InvoiceDto invoiceDto) {
        return Mono.just(invoiceDto)
                .flatMap(dto -> {
                    LocalDate dateOfReturnOfTheCar = Optional.ofNullable((invoiceDto.getCarDateOfReturn()))
                            .orElseThrow(() -> new SwiftWheelsHubException("Car return date is null"));

                    validateDateOfReturnOfTheCar(dateOfReturnOfTheCar);

                    if (Boolean.TRUE.equals(invoiceDto.getIsVehicleDamaged()) &&
                            ObjectUtils.isEmpty(invoiceDto.getDamageCost())) {
                        return Mono.error(new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "If the vehicle is damaged, the damage cost cannot be null/empty"
                                )
                        );
                    }

                    return Mono.just(dto);
                });
    }

    private Mono<Invoice> findEntityById(String id) {
        return invoiceRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Invoice with id " + id + " does not exist"
                                )
                        )
                );
    }

    private Flux<Invoice> findActiveInvoices() {
        Criteria criteria = Criteria.where("totalAmount").ne(null);
        Query query = new Query(criteria);

        return reactiveMongoTemplate.find(query, Invoice.class);
    }

    private Mono<Long> countActiveInvoices() {
        Criteria criteria = Criteria.where(TOTAL_AMOUNT).ne(null);
        Query query = new Query(criteria);

        return reactiveMongoTemplate.count(query, Long.class);
    }

    private void validateDateOfReturnOfTheCar(LocalDate dateOfReturnOfTheCar) {
        LocalDate currentDate = LocalDate.now();

        if (dateOfReturnOfTheCar.isBefore(currentDate)) {
            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Date of return of the car cannot be in the past"
            );
        }
    }

    private Invoice updateInvoiceWithBookingDetails(InvoiceDto invoiceDto, Tuple2<Invoice, BookingDto> existingInvoiceBookingDto) {
        Invoice existingInvoice = existingInvoiceBookingDto.getT1();
        BookingDto bookingDto = existingInvoiceBookingDto.getT2();
        String receptionistEmployeeIdAsString = invoiceDto.getReceptionistEmployeeId();
        String carId = invoiceDto.getCarId();
        ObjectId receptionistEmployeeId = MongoUtil.getObjectId(receptionistEmployeeIdAsString);
        Invoice existingInvoiceUpdated = updateExistingInvoice(existingInvoice, invoiceDto, carId, receptionistEmployeeId);

        return updateInvoiceAmount(bookingDto, existingInvoiceUpdated, bookingDto.getRentalCarPrice());
    }

    private Invoice updateExistingInvoice(Invoice existingInvoice, InvoiceDto invoiceDto, String carId,
                                          ObjectId receptionistEmployeeId) {
        existingInvoice.setCarDateOfReturn(invoiceDto.getCarDateOfReturn());
        existingInvoice.setReceptionistEmployeeId(receptionistEmployeeId);
        existingInvoice.setCarId(MongoUtil.getObjectId(carId));
        existingInvoice.setIsVehicleDamaged(invoiceDto.getIsVehicleDamaged());
        existingInvoice.setDamageCost(getDamageCost(invoiceDto));
        existingInvoice.setAdditionalPayment(getAdditionalPayment(invoiceDto));
        existingInvoice.setComments(invoiceDto.getComments());

        return existingInvoice;
    }

    private double getDamageCost(InvoiceDto invoiceDto) {
        return ObjectUtils.isEmpty(invoiceDto.getDamageCost()) ?
                0D : invoiceDto.getDamageCost().doubleValue();
    }

    private double getAdditionalPayment(InvoiceDto invoiceDto) {
        return ObjectUtils.isEmpty(invoiceDto.getAdditionalPayment()) ?
                0D : invoiceDto.getAdditionalPayment().doubleValue();
    }

    private BookingClosingDetailsDto getBookingClosingDetailsDto(Invoice invoice) {
        Boolean isVehicleDamaged = Optional.ofNullable(invoice.getIsVehicleDamaged())
                .orElseThrow(() -> new SwiftWheelsHubException("isVehicleDamaged is null"));

        BookingClosingDetailsDto bookingClosingDetailsDto = new BookingClosingDetailsDto();
        bookingClosingDetailsDto.bookingId(invoice.getBookingId().toString());
        bookingClosingDetailsDto.receptionistEmployeeId(invoice.getReceptionistEmployeeId().toString());
        bookingClosingDetailsDto.setCarStatus(getCarStatus(isVehicleDamaged));

        return bookingClosingDetailsDto;
    }

    private Double getTotalAmount(Invoice existingInvoice, BookingDto bookingDto, BigDecimal rentalCarPrice) {
        LocalDate carReturnDate = existingInvoice.getCarDateOfReturn();
        LocalDate bookingDateTo = bookingDto.getDateTo();
        LocalDate bookingDateFrom = bookingDto.getDateFrom();
        double carAmount = rentalCarPrice.doubleValue();

        boolean isReturnDatePassed = carReturnDate.isAfter(bookingDateTo);

        if (isReturnDatePassed) {
            return getMoneyForLateReturn(carReturnDate, bookingDateTo, bookingDateFrom, carAmount);
        }

        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                (ObjectUtils.isEmpty(existingInvoice.getDamageCost()) ? 0D : existingInvoice.getDamageCost());
    }

    private int getDaysPeriod(LocalDate bookingDateFrom, LocalDate bookingDateTo) {
        return Period.between(bookingDateFrom, bookingDateTo).getDays();
    }

    private double getMoneyForLateReturn(LocalDate carReturnDate, LocalDate bookingDateTo, LocalDate bookingDateFrom,
                                         Double carAmount) {
        return getDaysPeriod(bookingDateFrom, bookingDateTo) * carAmount +
                getDaysPeriod(bookingDateTo, carReturnDate) * 2 * carAmount;
    }

    private CarStatusEnum getCarStatus(boolean isVehicleDamaged) {
        return Boolean.TRUE.equals(isVehicleDamaged) ? CarStatusEnum.BROKEN : CarStatusEnum.AVAILABLE;
    }

    private Invoice updateInvoiceAmount(BookingDto bookingDto, Invoice existingInvoice, BigDecimal rentalCarPrice) {
        existingInvoice.setTotalAmount(getTotalAmount(existingInvoice, bookingDto, rentalCarPrice));

        return existingInvoice;
    }

}
