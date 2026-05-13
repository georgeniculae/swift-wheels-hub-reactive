package com.autohubreactive.invoicenotification.config;

import com.postmarkapp.postmark.Postmark;
import com.postmarkapp.postmark.client.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostmarkConfig {

    @Bean
    public ApiClient apiClient(@Value("${postmark.mail.api-key}") String apiKey) {
        return Postmark.getApiClient(apiKey, true);
    }

}
