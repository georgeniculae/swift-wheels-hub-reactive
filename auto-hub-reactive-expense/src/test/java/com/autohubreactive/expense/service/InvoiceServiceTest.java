package com.autohubreactive.expense.service;

import com.autohubreactive.dto.BookingResponse;
import com.autohubreactive.dto.InvoiceRequest;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.expense.mapper.InvoiceMapper;
import com.autohubreactive.expense.mapper.InvoiceMapperImpl;
import com.autohubreactive.expense.repository.InvoiceRepository;
import com.autohubreactive.expense.util.TestUtil;
import com.autohubreactive.model.Invoice;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @InjectMocks
    private InvoiceService invoiceService;

    @Mock
    private RevenueService revenueService;

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    private InvoiceRepository invoiceRepository;

    @Spy
    private InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void findAllInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.findAll()).thenReturn(Flux.just(invoice));

        invoiceService.findAllInvoices()
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();

        verify(invoiceMapper).mapEntityToDto(any(Invoice.class));
    }

    @Test
    void findAllInvoicesTest_errorOnFindingAll() {
        when(invoiceRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        invoiceService.findAllInvoices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findAllActiveInvoicesTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(reactiveMongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(Flux.just(invoice));

        invoiceService.findAllActiveInvoices()
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void findAllActiveInvoicesTest_errorOnFindingAllActiveInvoices() {
        when(reactiveMongoTemplate.find(any(Query.class), eq(Invoice.class))).thenReturn(Flux.error(new Throwable()));

        invoiceService.findAllActiveInvoices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findAllInvoicesByCustomerIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Flux.just(invoice));

        invoiceService.findAllInvoicesByCustomerUsername("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void findAllInvoicesByCustomerIdTest_errorOnFindingByCustomerUsername() {
        when(invoiceRepository.findByCustomerUsername(anyString())).thenReturn(Flux.error(new Throwable()));

        invoiceService.findAllInvoicesByCustomerUsername("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findInvoiceByIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));

        invoiceService.findInvoiceById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void findInvoiceByIdTest_errorOnFindingById() {
        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());

        invoiceService.findInvoiceById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findInvoiceByFilterTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.findByComments(anyString())).thenReturn(Flux.just(invoice));

        invoiceService.findInvoicesByComments("comment")
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void findInvoiceByFilterTest_errorOnFindingByComments() {
        when(invoiceRepository.findByComments(anyString())).thenReturn(Flux.empty());

        invoiceService.findInvoicesByComments("comment")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countInvoicesTest_success() {
        when(invoiceRepository.count()).thenReturn(Mono.just(3L));

        invoiceService.countInvoices()
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countInvoicesTest_errorOnCounting() {
        when(invoiceRepository.count()).thenReturn(Mono.error(new Throwable()));

        invoiceService.countInvoices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countAllActiveInvoicesTest_success() {
        when(reactiveMongoTemplate.count(any(Query.class), eq(Long.class))).thenReturn(Mono.just(3L));

        invoiceService.countAllActiveInvoices()
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countAllActiveInvoicesTest_errorOnCountingAllActiveInvoices() {
        when(reactiveMongoTemplate.count(any(Query.class), eq(Long.class))).thenReturn(Mono.error(new Throwable()));

        invoiceService.countAllActiveInvoices()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveInvoiceTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.existsByBookingId(any(ObjectId.class))).thenReturn(Mono.just(false));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.just(invoice));

        invoiceService.saveInvoice(bookingResponse)
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void saveInvoiceTest_error_existingInvoice() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(invoiceRepository.existsByBookingId(any(ObjectId.class))).thenReturn(Mono.just(false));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.error(new Throwable()));

        invoiceService.saveInvoice(bookingResponse)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void closeInvoiceTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/ClosedInvoice.json", Invoice.class);

        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/ClosedInvoiceResponse.json", InvoiceResponse.class);

        MockServerHttpRequest.get("/{id}", "64f361caf291ae086e179547")
                .header("Authorization", "token")
                .build();

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(revenueService.processClosing(any(Invoice.class))).thenReturn(Mono.just(invoice));

        invoiceService.closeInvoice("64f361caf291ae086e179547", invoiceRequest)
                .as(StepVerifier::create)
                .assertNext(actualInvoiceResponse -> assertThat(actualInvoiceResponse).usingRecursiveComparison().isEqualTo(invoiceResponse))
                .verifyComplete();
    }

    @Test
    void closeInvoiceTest_errorOnFindInvoiceById() {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        MockServerHttpRequest.get("/{id}", "64f361caf291ae086e179547")
                .header("Authorization", "token")
                .build();

        when(invoiceRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        invoiceService.closeInvoice("64f361caf291ae086e179547", invoiceRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenReturn(Mono.just(invoice));

        invoiceService.updateInvoiceAfterBookingUpdate(bookingResponse)
                .as(StepVerifier::create)
                .expectNext(invoiceResponse)
                .verifyComplete();
    }

    @Test
    void updateInvoiceAfterBookingUpdateTest_errorOnFindingByBookingId() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        invoiceService.updateInvoiceAfterBookingUpdate(bookingResponse)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/InProgressInvoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));
        when(invoiceRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_error_bookingInProgress() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.just(invoice));

        invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteInvoiceByBookingIdTest_errorOnFindingByBookingId() {
        when(invoiceRepository.findByBookingId(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        invoiceService.deleteInvoiceByBookingId("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
