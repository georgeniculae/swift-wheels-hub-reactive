package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import com.autohubreactive.invoicenotification.util.Constants;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final MustacheFactory mustacheFactory;

    @Value("${mail.from}")
    private String mailFrom;

    @Value("${mail.name}")
    private String name;

    public Mono<Void> sendEmail(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        return Mono.<Void>fromRunnable(() -> buildAndSend(toAddressEmail, invoiceResponse, pdfBytes))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void buildAndSend(String toAddressEmail, InvoiceResponse invoiceResponse, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailFrom, name);
            helper.setTo(toAddressEmail);
            helper.setSubject(Constants.SUBJECT);
            helper.setText(getMailBody(invoiceResponse));
            String filename = Constants.INVOICE_FILENAME_PREFIX + invoiceResponse.id() + Constants.PDF_EXTENSION;
            helper.addAttachment(filename, new ByteArrayResource(pdfBytes), Constants.APPLICATION_PDF_CONTENT_TYPE);

            mailSender.send(message);
        } catch (Exception e) {
            throw new AutoHubResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
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
