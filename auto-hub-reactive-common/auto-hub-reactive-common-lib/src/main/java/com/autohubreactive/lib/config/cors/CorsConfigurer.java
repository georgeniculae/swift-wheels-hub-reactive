package com.autohubreactive.lib.config.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Component
@EnableWebFlux
@RequiredArgsConstructor
@ConditionalOnProperty(name = "cors")
public class CorsConfigurer implements WebFluxConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry corsRegistry) {
        corsRegistry.addMapping(corsProperties.getMapping())
                .allowedOrigins(corsProperties.getAllowedOrigins())
                .allowedMethods(corsProperties.getAllowedMethods())
                .allowedHeaders(corsProperties.getAllowedHeaders());
    }

    @Override
    public void configureHttpMessageCodecs(@NonNull ServerCodecConfigurer serverCodecConfigurer) {
        serverCodecConfigurer.defaultCodecs()
                .maxInMemorySize(corsProperties.getCodecMaxInMemorySizeInMb() * 1024 * 1024);
    }

}
