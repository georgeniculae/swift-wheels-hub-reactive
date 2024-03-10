package com.swiftwheelshubreactive.emailnotification.service;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.swiftwheelshubreactive.dto.InvoiceResponse;
import com.swiftwheelshubreactive.emailnotification.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private SendGrid sendGrid;

    @Mock
    private MustacheFactory mustacheFactory;

    @Mock
    private Mustache mustache;

    @Mock
    private Writer writer;

    @Test
    void sendEmailTest_success() throws IOException {
        Mail mail = new Mail();
        Response response = new Response();

        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertDoesNotThrow(() -> emailService.sendEmail(mail));
    }

    @Test
    void createMailTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtils.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);

        Mail mail = emailService.createMail("test@email.com", invoiceResponse);

        assertNotNull(mail);
    }

}
