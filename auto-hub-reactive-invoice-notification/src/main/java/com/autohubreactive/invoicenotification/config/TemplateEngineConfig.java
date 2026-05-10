package com.autohubreactive.invoicenotification.config;

import com.autohubreactive.invoicenotification.util.Constants;
import org.apache.commons.lang.CharEncoding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class TemplateEngineConfig {

    @Bean
    public TemplateEngine templateEngine() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix(Constants.PDF_TEMPLATE);
        resolver.setSuffix(Constants.HTML_FILE_EXTENSION);
        resolver.setTemplateMode(TemplateMode.XML);
        resolver.setCharacterEncoding(CharEncoding.UTF_8);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(resolver);

        return templateEngine;
    }

}
