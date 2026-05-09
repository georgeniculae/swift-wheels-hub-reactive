package com.autohubreactive.emailnotification.handler;

import com.autohubreactive.emailnotification.router.InvoiceDocumentRouter;
import com.autohubreactive.emailnotification.service.InvoicePdfStorageService;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceDocumentHandlerTest {

    private WebTestClient webTestClient;

    @Mock
    private InvoicePdfStorageService invoicePdfStorageService;

    @BeforeEach
    void setUp() {
        InvoiceDocumentHandler handler = new InvoiceDocumentHandler(invoicePdfStorageService);
        InvoiceDocumentRouter router = new InvoiceDocumentRouter();
        webTestClient = WebTestClient
                .bindToRouterFunction(router.routeInvoiceDocument(handler))
                .build();
    }

    @Test
    void getInvoiceDocumentTest_success() {
        byte[] pdfBytes = new byte[]{1, 2, 3};

        when(invoicePdfStorageService.retrievePdf(anyString())).thenReturn(Mono.just(pdfBytes));

        webTestClient.get()
                .uri("/invoices/64f361caf291ae086e179547/document")
                .accept(MediaType.APPLICATION_PDF)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectBody(byte[].class)
                .isEqualTo(pdfBytes);
    }

    @Test
    void getInvoiceDocumentTest_notFound() {
        when(invoicePdfStorageService.retrievePdf(anyString()))
                .thenReturn(Mono.error(new AutoHubResponseStatusException(
                        HttpStatus.NOT_FOUND, "PDF not found")));

        webTestClient.get()
                .uri("/invoices/nonexistent/document")
                .accept(MediaType.APPLICATION_PDF)
                .exchange()
                .expectStatus().isNotFound();
    }

}
