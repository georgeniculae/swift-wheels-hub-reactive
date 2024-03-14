package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.CarMapper;
import com.swiftwheelshubreactive.agency.repository.CarRepository;
import com.swiftwheelshubreactive.agency.validator.CarRequestValidator;
import com.swiftwheelshubreactive.dto.BodyCategory;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.ExcelCarRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.BodyType;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import com.swiftwheelshubreactive.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String MAKE = "make";
    private static final String MODEL = "model";
    private static final String BODY_CATEGORY = "bodyCategory";
    private static final String YEAR_OF_PRODUCTION = "yearOfProduction";
    private static final String COLOR = "color";
    private static final String MILEAGE = "mileage";
    private static final String CAR_STATE = "carState";
    private static final String AMOUNT = "amount";
    private static final String ORIGINAL_BRANCH_ID = "originalBranchId";
    private static final String ACTUAL_BRANCH_ID = "actualBranchId";
    private static final String IMAGE = "image";
    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final ExcelProcessorService excelProcessorService;
    private final CarMapper carMapper;
    private final CarRequestValidator carRequestValidator;

    public Flux<CarResponse> findAllCars() {
        return carRepository.findAll()
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<CarResponse> findCarById(String id) {
        return findEntityById(id)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding car by id: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<CarResponse> findCarsByMake(String make) {
        return carRepository.findCarsByMake(make)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding cars by make: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<CarResponse> getAvailableCar(String id) {
        return findEntityById(id)
                .filter(car -> CarStatus.AVAILABLE.equals(car.getCarStatus()))
                .map(carMapper::mapEntityToDto)
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Selected car is not available"
                                )
                        )
                )
                .onErrorMap(e -> {
                    log.error("Error while getting available car: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<CarResponse> findCarsByFilterInsensitiveCase(String filter) {
        return carRepository.findAllByFilterInsensitiveCase(filter)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while searching car by criteria: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                })
                .switchIfEmpty(Mono.error(new SwiftWheelsHubResponseStatusException(HttpStatus.NOT_FOUND, "No result")));
    }

    public Mono<byte[]> getCarImage(String id) {
        return carRepository.findById(MongoUtil.getObjectId(id))
                .map(Car::getImage)
                .map(Binary::getData)
                .onErrorMap(e -> {
                    log.error("Error while getting car image: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<CarResponse> saveCar(MultiValueMap<String, Part> carRequestMultivalueMap) {
        return getCarRequest(carRequestMultivalueMap)
                .flatMap(carRequestValidator::validateBody)
                .flatMap(this::processNewCar)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving car: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Flux<CarResponse> uploadCars(FilePart filePart) {
        return filePart.content()
                .concatMap(this::getDataFromExcelAsPublisher)
                .concatMap(this::createNewCar)
                .collectList()
                .flatMapMany(carRepository::saveAll)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while uploading cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<CarResponse> updateCar(String id, MultiValueMap<String, Part> updatedCarRequestMultivalueMap) {
        return getCarRequest(updatedCarRequestMultivalueMap)
                .flatMap(carRequestValidator::validateBody)
                .flatMap(updatedCarRequest -> saveExistingCarUpdated(id, updatedCarRequest))
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<CarResponse> updateCarStatus(String id, CarState carState) {
        return findEntityById(id)
                .flatMap(car -> {
                    car.setCarStatus(CarStatus.valueOf(carState.name()));

                    return carRepository.save(car);
                })
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating car status cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Void> deleteCarById(String id) {
        return carRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorMap(e -> {
                    log.error("Error while deleting cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    public Mono<Long> countCars() {
        return carRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });

    }

    public Mono<CarResponse> updateCarWhenBookingIsClosed(String id, CarUpdateDetails carUpdateDetails) {
        return findEntityById(id)
                .zipWith(employeeService.findEntityById(carUpdateDetails.receptionistEmployeeId()))
                .map(carEmployee -> updateCarDetails(carUpdateDetails, carEmployee))
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                });
    }

    private Mono<Car> findEntityById(String id) {
        return carRepository.findById(MongoUtil.getObjectId(id))
                .onErrorMap(e -> {
                    log.error("Error while finding by id: {}", e.getMessage());

                    return new SwiftWheelsHubException(e.getMessage());
                })
                .switchIfEmpty(
                        Mono.error(
                                new SwiftWheelsHubResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Car with id " + id + " does not exist"
                                )
                        )
                );
    }

    private Mono<CarRequest> getCarRequest(MultiValueMap<String, Part> carRequestMultivalueMap) {
        return Mono.just(carRequestMultivalueMap.toSingleValueMap())
                .flatMap(fieldsAndValuesMap -> Mono.zip(
                                getFieldValuesAsListOfMono(fieldsAndValuesMap),
                                fieldsValue -> createCarRequest(fieldsAndValuesMap, fieldsValue)
                        )
                );
    }

    private List<Mono<String>> getFieldValuesAsListOfMono(Map<String, Part> fieldsAndValuesMap) {
        return List.of(
                convertPartToString(fieldsAndValuesMap.get(MAKE)),
                convertPartToString(fieldsAndValuesMap.get(MODEL)),
                convertPartToString(fieldsAndValuesMap.get(BODY_CATEGORY)),
                convertPartToString(fieldsAndValuesMap.get(YEAR_OF_PRODUCTION)),
                convertPartToString(fieldsAndValuesMap.get(COLOR)),
                convertPartToString(fieldsAndValuesMap.get(MILEAGE)),
                convertPartToString(fieldsAndValuesMap.get(CAR_STATE)),
                convertPartToString(fieldsAndValuesMap.get(AMOUNT)),
                convertPartToString(fieldsAndValuesMap.get(ORIGINAL_BRANCH_ID)),
                convertPartToString(fieldsAndValuesMap.get(ACTUAL_BRANCH_ID))
        );
    }

    private CarRequest createCarRequest(Map<String, Part> fieldsAndValuesMap, Object[] fieldsValue) {
        return CarRequest.builder()
                .make((String) fieldsValue[0])
                .model((String) fieldsValue[1])
                .bodyCategory(BodyCategory.valueOf((String) fieldsValue[2]))
                .yearOfProduction(Integer.parseInt((String) fieldsValue[3]))
                .color((String) fieldsValue[4])
                .mileage(Integer.parseInt((String) fieldsValue[5]))
                .carState(CarState.valueOf((String) fieldsValue[6]))
                .amount(BigDecimal.valueOf(Long.parseLong((String) fieldsValue[7])))
                .originalBranchId((String) fieldsValue[8])
                .actualBranchId((String) fieldsValue[9])
                .image((FilePart) fieldsAndValuesMap.get(IMAGE))
                .build();
    }

    private Mono<Car> processNewCar(CarRequest carRequest) {
        return getBranches(carRequest.originalBranchId(), carRequest.actualBranchId())
                .flatMap(originalBranchAndActualBranch -> setupNewCar(carRequest, originalBranchAndActualBranch));
    }

    private Mono<Car> setupNewCar(CarRequest carRequest, Tuple2<Branch, Branch> originalBranchAndActualBranch) {
        return getImageContent(carRequest.image())
                .switchIfEmpty(Mono.defer(() -> Mono.just(new byte[]{})))
                .flatMap(imageContent -> saveProcessedCar(carRequest, originalBranchAndActualBranch, imageContent));
    }

    private Mono<Car> saveProcessedCar(CarRequest carRequest, Tuple2<Branch, Branch> originalBranchAndActualBranch, byte[] imageContent) {
        Car newCar = carMapper.mapDtoToEntity(carRequest);
        newCar.setOriginalBranch(originalBranchAndActualBranch.getT1());
        newCar.setActualBranch(originalBranchAndActualBranch.getT2());

        if (imageContent.length != 0) {
            newCar.setImage(new Binary(BsonBinarySubType.BINARY, imageContent));
        }

        return carRepository.save(newCar);
    }

    private Mono<Car> saveExistingCarUpdated(String id, CarRequest updatedCarRequest) {
        return findEntityById(id)
                .zipWith(getBranches(updatedCarRequest.originalBranchId(), updatedCarRequest.actualBranchId()))
                .flatMap(existingCarAndOriginalBranchActualBranchTuple -> processCar(updatedCarRequest, existingCarAndOriginalBranchActualBranchTuple))
                .flatMap(carRepository::save);
    }

    private Mono<Car> processCar(CarRequest updatedCarRequest, Tuple2<Car, Tuple2<Branch, Branch>> existingCarAndOriginalBranchActualBranchTuple) {
        return getImageContent(updatedCarRequest.image())
                .switchIfEmpty(Mono.defer(() -> Mono.just(new byte[]{})))
                .map(imageContent -> updateExistingCar(updatedCarRequest, existingCarAndOriginalBranchActualBranchTuple.getT1(), existingCarAndOriginalBranchActualBranchTuple.getT2(), imageContent));
    }

    private Car updateExistingCar(CarRequest updatedCarRequest, Car existingCar,
                                  Tuple2<Branch, Branch> originalBranchAndActualBranch, byte[] imageContent) {
        existingCar.setMake(updatedCarRequest.make());
        existingCar.setModel(updatedCarRequest.model());
        existingCar.setBodyType(BodyType.valueOf(updatedCarRequest.bodyCategory().name()));
        existingCar.setYearOfProduction(updatedCarRequest.yearOfProduction());
        existingCar.setColor(updatedCarRequest.color());
        existingCar.setMileage(updatedCarRequest.mileage());
        existingCar.setAmount(updatedCarRequest.amount());
        existingCar.setCarStatus(CarStatus.valueOf(updatedCarRequest.carState().name()));
        existingCar.setOriginalBranch(originalBranchAndActualBranch.getT1());
        existingCar.setActualBranch(originalBranchAndActualBranch.getT2());

        if (imageContent.length != 0) {
            existingCar.setImage(new Binary(BsonBinarySubType.BINARY, imageContent));
        }

        return existingCar;
    }

    private Mono<String> convertPartToString(Part part) {
        return Mono.justOrEmpty(part)
                .flatMapMany(Part::content)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .reduce(StringUtils.EMPTY, (actual, next) -> actual + next);
    }

    private Mono<Tuple2<Branch, Branch>> getBranches(String originalBranchId, String actualBranchId) {
        return branchService.findEntityById(originalBranchId)
                .zipWith(branchService.findEntityById(actualBranchId));
    }

    private Car updateCarDetails(CarUpdateDetails carUpdateDetails, Tuple2<Car, Employee> carAndEmployee) {
        Car car = carAndEmployee.getT1();
        CarState carState = carUpdateDetails.carState();

        car.setActualBranch(carAndEmployee.getT2().getWorkingBranch());
        car.setCarStatus(CarStatus.valueOf(carState.name()));

        return car;
    }

    private Mono<byte[]> getImageContent(FilePart filePart) {
        return DataBufferUtils.join(getFilePartContent(filePart))
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                });
    }

    private Flux<DataBuffer> getFilePartContent(FilePart filePart) {
        return ObjectUtils.isEmpty(filePart) ? Flux.empty() : filePart.content();
    }

    private Flux<ExcelCarRequest> getDataFromExcelAsPublisher(DataBuffer dataBuffer) {
        return Mono.fromCallable(() -> excelProcessorService.extractDataFromExcel(dataBuffer.asInputStream()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<Car> createNewCar(ExcelCarRequest excelCarRequest) {
        return getBranches(excelCarRequest.originalBranchId(), excelCarRequest.actualBranchId())
                .map(originalBranchActualBranchTuple -> generateCar(excelCarRequest, originalBranchActualBranchTuple));
    }

    private Car generateCar(ExcelCarRequest excelCarRequest, Tuple2<Branch, Branch> originalBranchAndActualBranchTuple) {
        Car car = carMapper.mapExcelCarRequestToEntity(excelCarRequest);
        car.setOriginalBranch(originalBranchAndActualBranchTuple.getT1());
        car.setActualBranch(originalBranchAndActualBranchTuple.getT2());

        return car;
    }

}
