package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.BookingClosingDetailsDto;
import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.expense.mapper.InvoiceMapper;
import com.swiftwheelshub.expense.repository.InvoiceRepository;
import com.swiftwheelshub.expense.util.TestUtils;
import com.swiftwheelshub.model.Invoice;
import com.swiftwheelshub.expense.mapper.InvoiceMapperImpl;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private BookingService bookingService;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.findAll()).thenReturn(Flux.just(invoice));

        StepVerifier.create(invoiceService.findAllInvoices())
                .expectNext(invoiceDto)
                .verifyComplete();

        verify(invoiceMapper, times(1)).mapEntityToDto(any(Invoice.class));
    }

    @Test
    void findAllInvoicesTest_errorOnFindingAll() {
        when(invoiceRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(invoiceService.findAllInvoices())
                .expectError()
                .verify();
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(reactiveMongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(Flux.just(invoice));

        StepVerifier.create(invoiceService.findAllActiveInvoices())
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void findAllActiveInvoicesTest_errorOnFindingAllActiveInvoices() {
        when(reactiveMongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(invoiceService.findAllActiveInvoices())
                .expectError()
                .verify();
    }

    @Test
    void findAllInvoicesByCustomerIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(invoice));

        StepVerifier.create(invoiceService.findAllInvoicesByCustomerUsername("64f361caf291ae086e179547"))
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void findAllInvoicesByCustomerIdTest_errorOnFindingByCustomerUsername() {
        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(invoiceService.findAllInvoicesByCustomerUsername("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));

        StepVerifier.create(invoiceService.findInvoiceById("64f361caf291ae086e179547"))
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void findInvoiceByIdTest_errorOnFindingById() {
        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(invoiceService.findInvoiceById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.findByComments(anyString())).thenReturn(Flux.just(invoice));

        StepVerifier.create(invoiceService.findInvoicesByComments("comment"))
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void findInvoiceByFilterTest_errorOnFindingByComments() {
        when(invoiceRepository.findByComments(anyString())).thenReturn(Flux.empty());

        StepVerifier.create(invoiceService.findInvoicesByComments("comment"))
                .expectError()
                .verify();
    }

    @Test
    void countInvoicesTest_success() {
        when(invoiceRepository.count()).thenReturn(Mono.just(3L));

        StepVerifier.create(invoiceService.countInvoices())
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countInvoicesTest_errorOnCounting() {
        when(invoiceRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.countInvoices())
                .expectError()
                .verify();
    }

    @Test
    void countAllActiveInvoicesTest_success() {
        when(reactiveMongoTemplate.count(any(Query.class), eq(Long.class))).thenReturn(Mono.just(3L));

        StepVerifier.create(invoiceService.countAllActiveInvoices())
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countAllActiveInvoicesTest_errorOnCountingAllActiveInvoices() {
        when(reactiveMongoTemplate.count(any(Query.class), eq(Long.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.countAllActiveInvoices())
                .expectError()
                .verify();
    }

    @Test
    void saveInvoiceTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.existsByBookingId(any(ObjectId.class))).thenReturn(Mono.just(false));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.just(invoice));

        StepVerifier.create(invoiceService.saveInvoice(bookingDto))
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void saveInvoiceTest_error_existingInvoice() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(invoiceRepository.existsByBookingId(any(ObjectId.class))).thenReturn(Mono.just(false));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.saveInvoice(bookingDto))
                .expectError()
                .verify();
    }

    @Test
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        MockServerHttpRequest.get("/{id}", "64f361caf291ae086e179547")
                .header("Authorization", "token")
                .build();

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(bookingService.findBookingById(anyString(), anyString())).thenReturn(Mono.just(bookingDto));
        when(revenueService.saveInvoiceRevenueAndOutboxTransactional(any(Invoice.class))).thenReturn(Mono.just(invoice));
        when(bookingService.closeBooking(anyString(), any(BookingClosingDetailsDto.class)))
                .thenReturn(Mono.just(bookingDto));

        StepVerifier.create(invoiceService.closeInvoice("token", "64f361caf291ae086e179547", invoiceDto))
                .expectNext(invoiceDto)
                .verifyComplete();

        verify(invoiceMapper, times(1)).mapEntityToDto(any(Invoice.class));
    }

    @Test
    void closeInvoiceTest_errorOnFindInvoiceById() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        MockServerHttpRequest.get("/{id}", "64f361caf291ae086e179547")
                .header("Authorization", "token")
                .build();

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(bookingService.findBookingById(anyString(), anyString())).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.closeInvoice("token", "64f361caf291ae086e179547", invoiceDto))
                .expectError()
                .verify();
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);
        InvoiceDto invoiceDto = TestUtils.getResourceAsJson("/data/InvoiceDto.json", InvoiceDto.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.just(invoice));

        StepVerifier.create(invoiceService.updateInvoiceAfterBookingUpdate(bookingDto))
                .expectNext(invoiceDto)
                .verifyComplete();
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_errorOnFindingByBookingId() {
        BookingDto bookingDto = TestUtils.getResourceAsJson("/data/BookingDto.json", BookingDto.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.updateInvoiceAfterBookingUpdate(bookingDto))
                .expectError()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_success() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/InProgressInvoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(invoiceRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_error_bookingInProgress() {
        Invoice invoice = TestUtils.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));

        StepVerifier.create(invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_errorOnFindingByBookingId() {
        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
