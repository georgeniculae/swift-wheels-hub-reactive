package com.autohubreactive.invoicenotification.config;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGrid sendGrid(@Value("${sendgrid.mail.api-key}") String apiKey) {
        return new SendGrid(apiKey);
    }

}
