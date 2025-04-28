package com.autohubreactive.requestvalidator.util;

import com.autohubreactive.requestvalidator.model.SwaggerFile;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertSwaggerFile(SwaggerFile expectedSwaggerFile, SwaggerFile actualSwaggerFile) {
        assertEquals(expectedSwaggerFile.getIdentifier(), actualSwaggerFile.getIdentifier());
        assertEquals(expectedSwaggerFile.getSwaggerContent(), actualSwaggerFile.getSwaggerContent());
    }

}
