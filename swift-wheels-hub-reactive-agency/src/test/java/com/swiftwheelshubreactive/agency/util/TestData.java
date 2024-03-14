package com.swiftwheelshubreactive.agency.util;

import com.swiftwheelshubreactive.dto.BodyCategory;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.model.BodyType;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import com.swiftwheelshubreactive.model.RentalOffice;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
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

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TestData {
    public static CarRequest getCarRequest() {
        Path path = Paths.get("src/test/resources/image/car.jpg");
        Flux<DataBuffer> imageDataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        return CarRequest.builder()
                .make("Volkswagen")
                .model("Golf")
                .bodyCategory(BodyCategory.HATCHBACK)
                .yearOfProduction(2010)
                .color("black")
                .mileage(270000)
                .carState(CarState.AVAILABLE)
                .amount(BigDecimal.valueOf(500))
                .originalBranchId("64f361caf291ae086e179547")
                .actualBranchId("64f361caf291ae086e179547")
                .image(getFilePart("image", "car", imageDataBuffer))
                .build();
    }

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

    public static Car getCar() {
        Path path = Paths.get("src/test/resources/image/car.jpg");
        Flux<DataBuffer> imageDataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        return Car.builder()
                .make("Volkswagen")
                .model("Golf")
                .bodyType(BodyType.HATCHBACK)
                .yearOfProduction(2010)
                .color("black")
                .mileage(270000)
                .carStatus(CarStatus.AVAILABLE)
                .amount(BigDecimal.valueOf(500))
                .originalBranch(getBranch())
                .actualBranch(getBranch())
                .image(new Binary(getImageContent(getFilePart("image", "car", imageDataBuffer))))
                .build();
    }

    @SuppressWarnings("all")
    private static FilePart getFilePart(String name, String filename, Flux<DataBuffer> dataBuffer) {
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

    private static Branch getBranch() {
        return Branch.builder()
                .id(new ObjectId("64f361caf291ae086e179547"))
                .name("Test Branch")
                .address("Ploiesti")
                .rentalOffice(getRentalOffice())
                .build();
    }

    private static RentalOffice getRentalOffice() {
        return RentalOffice.builder()
                .id(new ObjectId("64f361caf291ae086e179547"))
                .name("Test Rental Office")
                .contactAddress("Ploiesti")
                .phoneNumber("")
                .build();
    }

    private static byte[] getImageContent(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                })
                .block();
    }

}
