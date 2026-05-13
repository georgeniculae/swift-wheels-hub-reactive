package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoicenotification.EmailResponse;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import com.autohubreactive.invoicenotification.mapper.EmailResponseMapper;
import com.autohubreactive.invoicenotification.util.Constants;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.StringWriter;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final ApiClient apiClient;
    private final MustacheFactory mustacheFactory;
    private final EmailResponseMapper emailResponseMapper;

    @Value("${postmark.mail.from}")
    private String mailFrom;

    @Value("${postmark.mail.name}")
    private String name;

    public Mono<EmailResponse> sendEmail(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        return Mono.fromCallable(() -> apiClient.deliverMessage(createMessage(toAddressEmail, invoiceResponse, pdfBytes)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(emailResponseMapper::mapToEmailResponse);
    }

    private Message createMessage(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        Message message = new Message();
        message.setFrom(name + " <" + mailFrom + ">");
        message.setTo(toAddressEmail);
        message.setSubject(Constants.SUBJECT);
        message.setTextBody(getMailBody(invoiceResponse));

        String filename = Constants.INVOICE_FILENAME_PREFIX + invoiceResponse.id() + Constants.PDF_EXTENSION;
        String encodedContent = Base64.getEncoder().encodeToString(pdfBytes);
        message.addAttachment(filename, encodedContent, Constants.APPLICATION_PDF_CONTENT_TYPE);

        return message;
    }

    private String getMailBody(Object object) {
        StringWriter stringWriter = new StringWriter();
        Mustache mustache = mustacheFactory.compile(Constants.MAIL_TEMPLATE_FOLDER + Constants.FILE_NAME + Constants.MUSTACHE_FILE_EXTENSION);

        try {
            mustache.execute(stringWriter, object).flush();
        } catch (Exception e) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return stringWriter.toString();
    }

}
