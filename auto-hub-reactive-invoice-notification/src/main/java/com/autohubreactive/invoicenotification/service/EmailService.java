package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoicenotification.EmailResponse;
import com.autohubreactive.invoicenotification.mapper.EmailResponseMapper;
import com.autohubreactive.invoicenotification.util.Constants;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
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

    private final SendGrid sendGrid;
    private final MustacheFactory mustacheFactory;
    private final EmailResponseMapper emailResponseMapper;

    @Value("${sendgrid.mail.from}")
    private String mailFrom;

    @Value("${sendgrid.mail.name}")
    private String name;

    public Mono<EmailResponse> sendEmail(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        return getMailResponse(createMail(toAddressEmail, invoiceResponse, pdfBytes))
                .map(emailResponseMapper::mapToEmailResponse);
    }

    private Mono<Response> getMailResponse(Mail mail) {
        return Mono.fromCallable(() -> sendMail(mail))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Response sendMail(Mail mail) {
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint(Constants.ENDPOINT);
            request.setBody(mail.build());

            return sendGrid.api(request);
        } catch (Exception e) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Mail createMail(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        Email from = new Email(mailFrom, name);
        Email to = new Email(toAddressEmail);
        Content content = new Content(Constants.CONTENT_TYPE, getMailBody(invoiceResponse));

        Mail mail = new Mail(from, Constants.SUBJECT, to, content);
        mail.setSubject(Constants.SUBJECT);

        Attachments attachment = new Attachments();
        attachment.setContent(Base64.getEncoder().encodeToString(pdfBytes));
        attachment.setType("application/pdf");
        attachment.setFilename("invoice-" + invoiceResponse.id() + ".pdf");
        attachment.setDisposition("attachment");
        mail.addAttachments(attachment);

        return mail;
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
