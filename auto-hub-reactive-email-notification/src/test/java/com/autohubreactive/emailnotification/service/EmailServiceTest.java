package com.autohubreactive.emailnotification.service;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.autohubreactive.dto.EmailResponse;
import com.autohubreactive.dto.InvoiceResponse;
import com.autohubreactive.emailnotification.mapper.EmailResponseMapper;
import com.autohubreactive.emailnotification.mapper.EmailResponseMapperImpl;
import com.autohubreactive.emailnotification.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
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

    @Spy
    private EmailResponseMapper emailResponseMapper = new EmailResponseMapperImpl();

    @Test
    void sendEmailTest_success() throws IOException {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Response response = new Response();
        response.setStatusCode(200);
        response.setBody("body");

        EmailResponse emailResponse =
                TestUtil.getResourceAsJson("/data/EmailResponse.json", EmailResponse.class);

        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);
        when(sendGrid.api(any(Request.class))).thenReturn(response);

        emailService.sendEmail("user@email.com", invoiceResponse)
                .as(StepVerifier::create)
                .expectNext(emailResponse)
                .verifyComplete();

        verify(emailResponseMapper).mapToEmailResponse(any(Response.class));
    }

    @Test
    void sendEmailTest_errorOnSendingRequest() throws IOException {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);
        when(sendGrid.api(any(Request.class))).thenThrow(new RuntimeException());

        emailService.sendEmail("user@email.com", invoiceResponse)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
