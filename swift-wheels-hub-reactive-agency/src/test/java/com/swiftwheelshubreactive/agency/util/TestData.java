package com.swiftwheelshubreactive.agency.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestData {

    public static MultiValueMap<String, Part> getCarRequestMultivalueMap() {
        Path path = Paths.get("src/test/resources/image/car.jpg");
        Flux<DataBuffer> imageDataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        MultiValueMap<String, Part> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.put("make", List.of(getFilePart("make", null, stringToDataBuffer("Volkswagen"))));
        multiValueMap.put("model", List.of(getFilePart("model", null, stringToDataBuffer("Golf"))));
        multiValueMap.put("bodyCategory", List.of(getFilePart("bodyCategory", null, stringToDataBuffer("HATCHBACK"))));
        multiValueMap.put("yearOfProduction", List.of(getFilePart("yearOfProduction", null, stringToDataBuffer("2010"))));
        multiValueMap.put("color", List.of(getFilePart("color", null, stringToDataBuffer("black"))));
        multiValueMap.put("mileage", List.of(getFilePart("mileage", null, stringToDataBuffer("270000"))));
        multiValueMap.put("carState", List.of(getFilePart("carState", null, stringToDataBuffer("AVAILABLE"))));
        multiValueMap.put("amount", List.of(getFilePart("amount", null, stringToDataBuffer("500"))));
        multiValueMap.put("originalBranchId", List.of(getFilePart("originalBranchId", null, stringToDataBuffer("64f361caf291ae086e179547"))));
        multiValueMap.put("actualBranchId", List.of(getFilePart("actualBranchId", null, stringToDataBuffer("64f361caf291ae086e179547"))));
        multiValueMap.put("image", List.of(getFilePart("image", "car", imageDataBuffer)));

        return multiValueMap;
    }

    @SuppressWarnings("all")
    private static FilePart getFilePart(String name, String filename,  Flux<DataBuffer> dataBuffer) {
        return new FilePart() {
            @Override
            public String name() {
                return name;
            }

            @Override
            public HttpHeaders headers() {
                return HttpHeaders.EMPTY;
            }

            @Override
            public Flux<DataBuffer> content() {
                return dataBuffer;
            }

            @Override
            public String filename() {
                return filename;
            }

            @Override
            public Mono<Void> transferTo(Path dest) {
                return null;
            }
        };
    }

    private static Flux<DataBuffer> stringToDataBuffer(String text) {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();
        DataBuffer dataBuffer = bufferFactory.wrap(text.getBytes());

        return Flux.just(dataBuffer);
    }

}
