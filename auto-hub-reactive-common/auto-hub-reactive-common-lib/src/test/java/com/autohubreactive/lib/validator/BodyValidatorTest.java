package com.autohubreactive.lib.validator;

import com.autohubreactive.dto.AuditLogInfoRequest;
import com.autohubreactive.lib.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class BodyValidatorTest {

    @InjectMocks
    private BodyValidator<AuditLogInfoRequest> bodyValidator;

    @Mock
    private Validator validator;

    @Test
    void validateBodyTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        doNothing().when(validator).validate(any(Object.class), any(Errors.class));

        bodyValidator.validateBody(auditLogInfoRequest)
                .as(StepVerifier::create)
                .expectNext(auditLogInfoRequest)
                .verifyComplete();
    }

}
