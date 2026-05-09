package com.autohubreactive.emailnotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.emailnotification.util.Constants;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TemplateEngine templateEngine;

    public Mono<byte[]> generatePdf(InvoiceResponse invoiceResponse) {
        return Mono.fromCallable(() -> renderPdf(invoiceResponse))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private byte[] renderPdf(InvoiceResponse invoiceResponse) {
        Context context = new Context();
        context.setVariable(Constants.INVOICE, invoiceResponse);
        String html = templateEngine.process(Constants.INVOICE_PDF, context);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new AutoHubResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
