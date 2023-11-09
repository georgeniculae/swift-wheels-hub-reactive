package com.carrental.cloudgateway.service;

import com.carrental.lib.exceptionhandling.CarRentalException;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwaggerExtractorService {

    private static final String HYPHEN_REGEX = "-";

    @Value("${swagger.location}")
    private String swaggerLocation;

    private final ResourceLoader resourceLoader;

    public Mono<Map<String, OpenAPI>> getSwaggerIdentifierAndContent() {
        return Mono.fromCallable(() -> resourceLoader.getResource(validateSwaggerLocation()))
                .map(this::getSwaggerIdentifierAndContent);
    }

    private String validateSwaggerLocation() {
        return Optional.ofNullable(swaggerLocation)
                .orElseThrow(() -> new CarRentalException("Swagger location is empty"));
    }

    private Map<String, OpenAPI> getSwaggerIdentifierAndContent(Resource resource) {
        return getFilesPaths(resource).stream()
                .collect(Collectors.toMap(this::getSwaggerIdentifier, this::extractSwaggerFile));
    }

    private List<Path> getFilesPaths(Resource resource) {
        try {
            Path path = Paths.get(resource.getURI());

            try (Stream<Path> lines = Files.list(path)) {
                return lines.toList();
            }
        } catch (Exception e) {
            throw new CarRentalException(e);
        }
    }

    private String getSwaggerIdentifier(Path swaggerFilePath) {
        String[] split = FilenameUtils.removeExtension(swaggerFilePath.getFileName().toString()).split(HYPHEN_REGEX);

        return split[split.length - 1];
    }

    private OpenAPI extractSwaggerFile(Path swaggerFilePath) {
        return new OpenAPIV3Parser().read(swaggerFilePath.toUri().toString());
    }

}
