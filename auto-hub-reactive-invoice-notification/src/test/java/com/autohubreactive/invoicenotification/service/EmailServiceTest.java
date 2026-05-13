package com.autohubreactive.invoicenotification.service;

import com.autohubreactive.dto.common.InvoiceResponse;
import com.autohubreactive.dto.invoicenotification.EmailResponse;
import com.autohubreactive.invoicenotification.mapper.EmailResponseMapper;
import com.autohubreactive.invoicenotification.mapper.EmailResponseMapperImpl;
import com.autohubreactive.invoicenotification.util.TestUtil;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.postmarkapp.postmark.client.ApiClient;
import com.postmarkapp.postmark.client.data.model.message.Message;
import com.postmarkapp.postmark.client.data.model.message.MessageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

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
    private ApiClient apiClient;

    @Mock
    private MustacheFactory mustacheFactory;

    @Mock
    private Mustache mustache;

    @Mock
    private Writer writer;

    @Spy
    private EmailResponseMapper emailResponseMapper = new EmailResponseMapperImpl();

    @Test
    void sendEmailTest_success() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setErrorCode(0);
        messageResponse.setMessage("OK");

        EmailResponse emailResponse =
                TestUtil.getResourceAsJson("/data/EmailResponse.json", EmailResponse.class);

        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);
        when(apiClient.deliverMessage(any(Message.class))).thenReturn(messageResponse);

        emailService.sendEmail("user@email.com", invoiceResponse, new byte[]{1, 2, 3})
                .as(StepVerifier::create)
                .expectNext(emailResponse)
                .verifyComplete();

        verify(emailResponseMapper).mapToEmailResponse(any(MessageResponse.class));
    }

    @Test
    void sendEmailTest_errorOnSendingRequest() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(mustacheFactory.compile(anyString())).thenReturn(mustache);
        when(mustache.execute(any(StringWriter.class), any(Object.class))).thenReturn(writer);
        when(apiClient.deliverMessage(any(Message.class))).thenThrow(new RuntimeException());

        emailService.sendEmail("user@email.com", invoiceResponse, new byte[]{1, 2, 3})
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
