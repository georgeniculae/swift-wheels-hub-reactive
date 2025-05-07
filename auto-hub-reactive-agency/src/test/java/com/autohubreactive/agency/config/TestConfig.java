package com.autohubreactive.agency.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

@TestConfiguration
public class TestConfig {

    @Bean
    public Validator validator() {
        return new CustomValidatorBean();
    }

    @Bean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

}
