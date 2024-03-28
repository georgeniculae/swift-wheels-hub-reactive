package com.swiftwheelshubreactive.emailnotification.service;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.StringWriter;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String CONTENT_TYPE = "text/plain";

    private static final String SUBJECT = "Invoice Notice";

    private static final String ENDPOINT = "mail/send";

    private static final String MAIL_TEMPLATE_FOLDER = "mail-template/";

    private static final String FILE_NAME = "invoice-notice";

    private static final String MUSTACHE_FORMAT = ".mustache";

    @Value("${sendgrid.mail.from}")
    private String mailFrom;

    @Value("${sendgrid.mail.name}")
    private String name;

    private final SendGrid sendGrid;

    private final MustacheFactory mustacheFactory;

    public Mono<Response> sendEmail(String toAddressEmail, Object object) {
        return getMailAsPublisher(toAddressEmail, object)
                .flatMap(this::getMailResponseAsPublisher);
    }

    private Mono<Response> getMailResponseAsPublisher(Mail mail) {
        return Mono.fromCallable(() -> sendMail(mail))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Mono<Mail> getMailAsPublisher(String toAddressEmail, Object object) {
        return Mono.fromCallable(() -> createMail(toAddressEmail, object))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private Response sendMail(Mail mail) {
        Request request = new Request();

        request.setMethod(Method.POST);
        request.setEndpoint(ENDPOINT);

        try {
            request.setBody(mail.build());

            return sendGrid.api(request);
        } catch (Exception e) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private Mail createMail(String toAddressEmail, Object object) {
        Email from = new Email(mailFrom, name);
        Email to = new Email(toAddressEmail);

        Content content = new Content(CONTENT_TYPE, getMailBody(object));

        Mail mail = new Mail(from, SUBJECT, to, content);
        mail.setSubject(SUBJECT);

        return mail;
    }

    private String getMailBody(Object object) {
        StringWriter stringWriter = new StringWriter();

        Mustache mustache = mustacheFactory.compile(MAIL_TEMPLATE_FOLDER + FILE_NAME + MUSTACHE_FORMAT);
        try {
            mustache.execute(stringWriter, object).flush();
        } catch (Exception e) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        return stringWriter.toString();
    }

}
