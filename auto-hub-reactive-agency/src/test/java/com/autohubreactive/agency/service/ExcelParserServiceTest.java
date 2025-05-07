package com.autohubreactive.agency.service;

import com.autohubreactive.dto.agency.ExcelCarRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static com.mongodb.assertions.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class ExcelParserServiceTest {

    @InjectMocks
    private ExcelParserService excelParserService;

    @Test
    void extractDataFromExcelTest_success() {
        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        List<ExcelCarRequest> excelCarRequests =
                excelParserService.extractDataFromExcel(Objects.requireNonNull(dataBuffer.blockFirst()).asInputStream());

        assertFalse(excelCarRequests.isEmpty());
    }

}
