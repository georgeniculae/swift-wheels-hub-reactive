package com.swiftwheelshubreactive.agency.service;

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
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
class ExcelProcessorServiceTest {

    @InjectMocks
    private ExcelProcessorService excelProcessorService;

    @Test
    void extractDataFromExcelTest_success() {
        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        excelProcessorService.extractDataFromExcel(Objects.requireNonNull(dataBuffer.blockFirst()).asInputStream());
    }

}
