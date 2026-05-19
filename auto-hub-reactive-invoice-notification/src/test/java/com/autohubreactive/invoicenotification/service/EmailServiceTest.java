package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.invoicenotification.util.TestUtil;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import java.io.Writer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MustacheFactory mustacheFactory;

    @Mock
    private Mustache mustache;

    @Mock
    private Writer writer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "mailFrom", "from@example.com");
        ReflectionTestUtils.setField(emailService, "name", "Auto Hub");
    }

    @Test
    void sendEmailTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(Writer.class), any(Object.class))).thenReturn(writer);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        emailService.sendEmail("user@email.com", invoiceResponse, new byte[]{1, 2, 3})
                .as(StepVerifier::create)
                .verifyComplete();

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendEmailTest_errorOnSendingRequest() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(Writer.class), any(Object.class))).thenReturn(writer);
        doThrow(new MailSendException("error")).when(mailSender).send(any(MimeMessage.class));

        emailService.sendEmail("user@email.com", invoiceResponse, new byte[]{1, 2, 3})
                .as(StepVerifier::create)
                .expectError(MailSendException.class)
                .verify();
    }

}
