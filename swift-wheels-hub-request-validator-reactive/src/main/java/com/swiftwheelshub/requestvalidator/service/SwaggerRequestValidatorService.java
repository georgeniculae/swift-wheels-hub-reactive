package com.swiftwheelshub.requestvalidator.service;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.model.SimpleRequest;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.whitelist.ValidationErrorsWhitelist;
import com.atlassian.oai.validator.whitelist.rule.WhitelistRules;
import com.swiftwheelshub.dto.IncomingRequestDetails;
import com.swiftwheelshub.dto.RequestValidationReport;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.requestvalidator.model.SwaggerFolder;
import com.swiftwheelshub.requestvalidator.repository.SwaggerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class SwaggerRequestValidatorService {

    private static final String SWAGGER = "swagger";

    private static final String V3 = "v3";

    private static final String SWAGGER_PATH = "Swagger path";

    private static final String SWAGGER_MESSAGE = "Swagger message";

    private static final String V3_PATH = "v3 path";

    private static final String V3_MESSAGE = "v3 message";

    private static final String SEPARATOR_REGEX = "/";

    private final SwaggerRepository swaggerRepository;

    public RequestValidationReport validateRequest(IncomingRequestDetails request) {
        SimpleRequest simpleRequest = getSimpleRequest(request);
        ValidationReport validationReport = getValidationReport(simpleRequest);

        return new RequestValidationReport(getValidationErrorMessage(validationReport));
    }

    private SimpleRequest getSimpleRequest(IncomingRequestDetails request) {
        SimpleRequest.Builder simpleRequestBuilder = new SimpleRequest.Builder(request.method(), request.path());

        request.headers()
                .forEach(simpleRequestBuilder::withHeader);

        request.queryParams()
                .forEach(simpleRequestBuilder::withQueryParam);

        simpleRequestBuilder.withBody(request.body());

        return simpleRequestBuilder.build();
    }

    private ValidationReport getValidationReport(SimpleRequest simpleRequest) {
        SwaggerFolder swaggerFolder = swaggerRepository.findById(getMicroserviceIdentifier(simpleRequest))
                .orElseThrow(() -> new SwiftWheelsHubException("Swagger folder does not exist"));

        String swaggerFile = swaggerFolder.getSwaggerContent();
        OpenApiInteractionValidator validator = OpenApiInteractionValidator.createForInlineApiSpecification(swaggerFile)
                .withWhitelist(getWhitelist())
                .build();

        return validator.validateRequest(simpleRequest);
    }

    private String getMicroserviceIdentifier(SimpleRequest simpleRequest) {
        return simpleRequest.getPath().replaceFirst(SEPARATOR_REGEX, StringUtils.EMPTY).split(SEPARATOR_REGEX)[0];
    }

    private String getValidationErrorMessage(ValidationReport validationReport) {
        return validationReport.getMessages()
                .stream()
                .map(ValidationReport.Message::getMessage)
                .collect(Collectors.joining());
    }

    private ValidationErrorsWhitelist getWhitelist() {
        return ValidationErrorsWhitelist.create()
                .withRule(SWAGGER_PATH, WhitelistRules.pathContainsSubstring(SWAGGER))
                .withRule(SWAGGER_MESSAGE, WhitelistRules.messageContainsSubstring(SWAGGER))
                .withRule(V3_PATH, WhitelistRules.pathContainsSubstring(V3))
                .withRule(V3_MESSAGE, WhitelistRules.messageContainsSubstring(V3));
    }

}
