package com.swiftwheelshubreactive.agency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftwheelshubreactive.agency.mapper.CarMapper;
import com.swiftwheelshubreactive.agency.repository.CarRepository;
import com.swiftwheelshubreactive.agency.validator.CarRequestValidator;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.ExcelCarRequest;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.lib.exceptionhandling.ExceptionUtil;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
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
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private static final String IMAGE = "image";
    private static final String CAR_REQUEST = "carRequest";
    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final ExcelProcessorService excelProcessorService;
    private final CarMapper carMapper;
    private final CarRequestValidator carRequestValidator;
    private final ObjectMapper objectMapper;

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

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<CarResponse> findCarsByMakeInsensitiveCase(String make) {
        return carRepository.findCarsByMakeInsensitiveCase(make)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding cars by make: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
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

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<CarResponse> getAllAvailableCars() {
        return carRepository.findAllAvailableCars()
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while getting all available cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<CarResponse> findCarsByFilterInsensitiveCase(String filter) {
        return carRepository.findAllByFilterInsensitiveCase(filter)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while searching car by criteria: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                })
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("No matching car for filter: " + filter)));
    }

    public Mono<byte[]> getCarImage(String id) {
        return carRepository.findImageByCarId(MongoUtil.getObjectId(id))
                .map(Car::getImage)
                .map(Binary::getData)
                .onErrorMap(e -> {
                    log.error("Error while getting car image: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<CarResponse> saveCar(Map<String, Part> carRequestPartMap) {
        return getCarRequest(carRequestPartMap.get(CAR_REQUEST))
                .flatMap(carRequestValidator::validateBody)
                .flatMap(carRequest -> setupNewCar(carRequestPartMap, carRequest))
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving car: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
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

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<CarResponse> updateCar(String id, Map<String, Part> carRequestPartMap) {
        return getCarRequest(carRequestPartMap.get(CAR_REQUEST))
                .flatMap(carRequestValidator::validateBody)
                .flatMap(updatedCarRequest -> setupUpdatedCar(id, carRequestPartMap, updatedCarRequest))
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<CarResponse> updateCarStatus(String id, CarState carState) {
        return findEntityById(id)
                .doOnNext(car -> car.setCarStatus(CarStatus.valueOf(carState.name())))
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating car status cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<CarResponse> updateCarsStatus(List<UpdateCarRequest> updateCarRequests) {
        return carRepository.findAllById(getCarIds(updateCarRequests))
                .doOnNext(car -> car.setCarStatus(getUpdatedCarStatus(updateCarRequests, car)))
                .collectList()
                .flatMapMany(carRepository::saveAll)
                .map(carMapper::mapEntityToDto);
    }

    public Mono<Void> deleteCarById(String id) {
        return carRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorMap(e -> {
                    log.error("Error while deleting cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Long> countCars() {
        return carRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });

    }

    public Mono<CarResponse> updateCarWhenBookingIsClosed(String id, CarUpdateDetails carUpdateDetails) {
        return setupFinalCarDetails(id, carUpdateDetails)
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating car: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    private Mono<Car> findEntityById(String id) {
        return carRepository.findById(MongoUtil.getObjectId(id))
                .onErrorMap(e -> {
                    log.error("Error while finding by id: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                })
                .switchIfEmpty(Mono.error(new SwiftWheelsHubNotFoundException("Car with id " + id + " does not exist")));
    }

    private Mono<CarRequest> getCarRequest(Part carRequestAsPart) {
        return Mono.fromSupplier(() -> {
            FormFieldPart carRequestFormFieldPart = (FormFieldPart) carRequestAsPart;

            try {
                return objectMapper.readValue(carRequestFormFieldPart.value(), CarRequest.class);
            } catch (JsonProcessingException e) {
                throw new SwiftWheelsHubException(e.getMessage());
            }
        });
    }

    private Mono<Car> setupNewCar(Map<String, Part> carRequestPartMap, CarRequest carRequest) {
        return Mono.zip(
                List.of(
                        branchService.findEntityById(carRequest.originalBranchId()),
                        branchService.findEntityById(carRequest.actualBranchId()),
                        getImageContent(carRequestPartMap.get(IMAGE))),
                values -> getNewCarInstance(carRequest, values)
        );
    }

    private Car getNewCarInstance(CarRequest carRequest, Object[] values) {
        Branch originalBranch = (Branch) values[0];
        Branch actualBranch = (Branch) values[1];
        byte[] imageContent = (byte[]) values[2];

        Car car = carMapper.mapDtoToEntity(carRequest, imageContent);
        car.setOriginalBranch(originalBranch);
        car.setActualBranch(actualBranch);

        return car;
    }

    private CarStatus getUpdatedCarStatus(List<UpdateCarRequest> updateCarRequests, Car car) {
        UpdateCarRequest matchingUpdateCarRequest = updateCarRequests.stream()
                .filter(updateCarRequest -> car.getId().toString().equals(updateCarRequest.carId()))
                .findAny()
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Car details not found"));

        return CarStatus.valueOf(matchingUpdateCarRequest.carState().name());
    }

    private List<ObjectId> getCarIds(List<UpdateCarRequest> updateCarRequests) {
        return updateCarRequests.stream()
                .map(updateCarRequest -> MongoUtil.getObjectId(updateCarRequest.carId()))
                .toList();
    }

    private Mono<Car> setupUpdatedCar(String id, Map<String, Part> carRequestPartMap, CarRequest updatedCarRequest) {
        return Mono.zip(
                List.of(
                        findEntityById(id),
                        branchService.findEntityById(updatedCarRequest.originalBranchId()),
                        branchService.findEntityById(updatedCarRequest.actualBranchId()),
                        getImageContent(carRequestPartMap.get(IMAGE))),
                values -> {
                    Car existingCar = (Car) values[0];
                    Branch originalBranch = (Branch) values[1];
                    Branch actualBranch = (Branch) values[2];
                    byte[] imageContent = (byte[]) values[3];

                    return updateExistingCar(updatedCarRequest, existingCar, originalBranch, actualBranch, imageContent);
                }
        );
    }

    private Car updateExistingCar(CarRequest updatedCarRequest, Car existingCar, Branch originalBranch,
                                  Branch actualBranch, byte[] imageContent) {
        existingCar.setMake(updatedCarRequest.make());
        existingCar.setModel(updatedCarRequest.model());
        existingCar.setBodyType(BodyType.valueOf(updatedCarRequest.bodyCategory().name()));
        existingCar.setYearOfProduction(updatedCarRequest.yearOfProduction());
        existingCar.setColor(updatedCarRequest.color());
        existingCar.setMileage(updatedCarRequest.mileage());
        existingCar.setAmount(updatedCarRequest.amount());
        existingCar.setCarStatus(CarStatus.valueOf(updatedCarRequest.carState().name()));
        existingCar.setOriginalBranch(originalBranch);
        existingCar.setActualBranch(actualBranch);

        if (imageContent.length != 0) {
            existingCar.setImage(new Binary(BsonBinarySubType.BINARY, imageContent));
        }

        return existingCar;
    }

    private Mono<Car> setupFinalCarDetails(String id, CarUpdateDetails carUpdateDetails) {
        return Mono.zip(
                findEntityById(id),
                employeeService.findEntityById(carUpdateDetails.receptionistEmployeeId()),
                (existingCar, receptionistEmployee) -> updateCarDetails(carUpdateDetails, existingCar, receptionistEmployee)
        );
    }

    private Car updateCarDetails(CarUpdateDetails carUpdateDetails, Car car, Employee employee) {
        CarState carState = carUpdateDetails.carState();

        car.setActualBranch(employee.getWorkingBranch());
        car.setCarStatus(CarStatus.valueOf(carState.name()));

        return car;
    }

    private Mono<byte[]> getImageContent(Part part) {
        return DataBufferUtils.join(getPartContent(part))
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(new byte[]{})));
    }

    private Flux<DataBuffer> getPartContent(Part part) {
        return ObjectUtils.isEmpty(part) ? Flux.empty() : part.content();
    }

    private Flux<ExcelCarRequest> getDataFromExcelAsPublisher(DataBuffer dataBuffer) {
        return Mono.fromCallable(() -> excelProcessorService.extractDataFromExcel(dataBuffer.asInputStream()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<Car> createNewCar(ExcelCarRequest excelCarRequest) {
        return Mono.zip(
                branchService.findEntityById(excelCarRequest.originalBranchId()),
                branchService.findEntityById(excelCarRequest.actualBranchId()),
                (originalBranch, actualBranch) -> generateCar(excelCarRequest, originalBranch, actualBranch));
    }

    private Car generateCar(ExcelCarRequest excelCarRequest, Branch originalBranch, Branch actualBranch) {
        Car car = carMapper.mapExcelCarRequestToEntity(excelCarRequest);
        car.setOriginalBranch(originalBranch);
        car.setActualBranch(actualBranch);

        return car;
    }

}
