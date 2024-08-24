package com.swiftwheelshubreactive.lib.config.global;

import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Component
@EnableWebFlux
public class CorsConfigurer implements WebFluxConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }

    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer serverCodecConfigurer) {
        serverCodecConfigurer.defaultCodecs().maxInMemorySize(20480 * 1024);
    }

}
