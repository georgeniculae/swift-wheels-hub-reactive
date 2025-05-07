package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.CarMapper;
import com.autohubreactive.agency.repository.CarRepository;
import com.autohubreactive.agency.validator.CarRequestValidator;
import com.autohubreactive.dto.common.AvailableCarInfo;
import com.autohubreactive.dto.agency.CarRequest;
import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.dto.common.CarState;
import com.autohubreactive.dto.common.CarStatusUpdate;
import com.autohubreactive.dto.common.CarUpdateDetails;
import com.autohubreactive.dto.agency.ExcelCarRequest;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.exception.AutoHubResponseStatusException;
import com.autohubreactive.lib.exceptionhandling.ExceptionUtil;
import com.autohubreactive.lib.util.MongoUtil;
import com.autohubreactive.model.agency.Branch;
import com.autohubreactive.model.agency.Car;
import com.autohubreactive.model.agency.CarStatus;
import com.autohubreactive.model.agency.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
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
    private final ExcelParserService excelParserService;
    private final ReactiveGridFsTemplate reactiveGridFsTemplate;
    private final CarMapper carMapper;
    private final CarRequestValidator carRequestValidator;
    private final ObjectMapper objectMapper;

    public Flux<CarResponse> findAllCars() {
        return carRepository.findAll()
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while finding all cars: {}", e.getMessage());

                    return new AutoHubException(e.getMessage());
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

    public Mono<AvailableCarInfo> getAvailableCar(String id) {
        return findEntityById(id)
                .filter(car -> CarStatus.AVAILABLE.equals(car.getCarStatus()))
                .map(carMapper::mapToAvailableCarInfo)
                .switchIfEmpty(Mono.error(getCarNotAvailableException()))
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
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("No matching car for filter: " + filter)))
                .onErrorMap(e -> {
                    log.error("Error while searching car by criteria: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<byte[]> getCarImage(String id) {
        return DataBufferUtils.join(getDataBufferImage(id))
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);

                    return bytes;
                })
                .onErrorMap(e -> {
                    log.error("Error while getting car image: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<CarResponse> saveCar(Map<String, Part> carRequestPartMap) {
        return getCarRequest(carRequestPartMap.get(CAR_REQUEST))
                .flatMap(carRequestValidator::validateBody)
                .flatMap(this::setupNewCar)
                .flatMap(carRepository::save)
                .delayUntil(car -> saveCarImage(carRequestPartMap, car))
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while saving car: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Flux<CarResponse> uploadCars(FilePart filePart) {
        return filePart.content()
                .concatMap(this::extractCarsFromExcelRows)
                .flatMap(this::processCarFromExcel)
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while uploading cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<CarResponse> updateCar(String id, Map<String, Part> carRequestPartMap) {
        return getCarRequest(carRequestPartMap.get(CAR_REQUEST))
                .flatMap(carRequestValidator::validateBody)
                .flatMap(updatedCarRequest -> setupUpdatedCar(id, updatedCarRequest))
                .flatMap(carRepository::save)
                .delayUntil(car -> saveCarImage(carRequestPartMap, car))
                .map(carMapper::mapEntityToDto)
                .onErrorMap(e -> {
                    log.error("Error while updating cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> updateCarStatus(CarStatusUpdate carStatusUpdate) {
        return findEntityById(carStatusUpdate.carId())
                .map(existingCar -> {
                    CarStatus carStatus = CarStatus.valueOf(carStatusUpdate.carState().name());

                    return carMapper.getUpdatedCarWithStatus(existingCar, carStatus);
                })
                .flatMap(carRepository::save)
                .then()
                .onErrorResume(e -> {
                    log.error("Error while updating car {} status: {}", carStatusUpdate.carId(), e.getMessage());

                    return Mono.error(new AutoHubException(e.getMessage()));
                });
    }

    public Mono<Void> updateCarsStatus(UpdateCarsRequest updateCarsRequests) {
        return carRepository.findAllById(getCarIds(updateCarsRequests))
                .map(existingCar -> getUpdatedCar(updateCarsRequests, existingCar))
                .collectList()
                .flatMapMany(carRepository::saveAll)
                .then()
                .onErrorResume(e -> {
                    log.error("Error while updating cars statuses: {}", e.getMessage());

                    return Mono.error(new AutoHubException(e.getMessage()));
                });
    }

    public Mono<Void> deleteCarById(String id) {
        return carRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorMap(e -> {
                    log.error("Error while deleting cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });
    }

    public Mono<Void> updateCarWhenBookingIsClosed(CarUpdateDetails carUpdateDetails) {
        return updateCarAfterClosingBooking(carUpdateDetails)
                .flatMap(carRepository::save)
                .then();
    }

    public Mono<Long> countCars() {
        return carRepository.count()
                .onErrorMap(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return ExceptionUtil.handleException(e);
                });

    }

    private Mono<Car> findEntityById(String id) {
        return carRepository.findById(MongoUtil.getObjectId(id))
                .switchIfEmpty(Mono.error(new AutoHubNotFoundException("Car with id " + id + " does not exist")));
    }

    private Mono<Car> findCarInUse(String id) {
        return carRepository.findCarByIdAndCarStatus(MongoUtil.getObjectId(id), CarStatus.NOT_AVAILABLE);
    }

    private Mono<ObjectId> saveCarImage(Map<String, Part> carRequestPartMap, Car car) {
        return Mono.justOrEmpty(carRequestPartMap.get(IMAGE))
                .flatMap(part -> saveCarImage(getDataBuffer(part), car.getId().toString()));
    }

    private Mono<Car> processCarFromExcel(ExcelCarRequest excelCarRequest) {
        return createNewCarFromExcelData(excelCarRequest)
                .flatMap(carRepository::save)
                .delayUntil(car -> saveExcelImage(excelCarRequest, car));
    }

    private Mono<ObjectId> saveExcelImage(ExcelCarRequest excelCarRequest, Car car) {
        return Mono.just(excelCarRequest.image())
                .filter(image -> image.length > 0)
                .flatMap(image -> saveCarImage(getImageAsDataBuffer(image), car.getId().toString()));
    }

    private Flux<DataBuffer> getImageAsDataBuffer(byte[] bytes) {
        DataBufferFactory bufferFactory = new DefaultDataBufferFactory();

        DataBuffer buffer = bufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);

        return Flux.just(buffer);
    }

    private Flux<DataBuffer> getDataBufferImage(String id) {
        return reactiveGridFsTemplate.find(Query.query(GridFsCriteria.whereFilename().is(id)))
                .flatMap(reactiveGridFsTemplate::getResource)
                .flatMap(ReactiveGridFsResource::getDownloadStream);
    }

    private Mono<CarRequest> getCarRequest(Part carRequestAsPart) {
        return Mono.fromSupplier(() -> {
            FormFieldPart carRequestFormFieldPart = (FormFieldPart) carRequestAsPart;

            try {
                return objectMapper.readValue(carRequestFormFieldPart.value(), CarRequest.class);
            } catch (JsonProcessingException e) {
                throw new AutoHubException(e.getMessage());
            }
        });
    }

    private Mono<Car> setupNewCar(CarRequest carRequest) {
        return Mono.zip(
                branchService.findEntityById(carRequest.originalBranchId()),
                branchService.findEntityById(carRequest.actualBranchId()),
                (originalBranch, actualBranch) -> carMapper.getNewCar(carRequest, originalBranch, actualBranch)
        );
    }

    private Car getUpdatedCar(UpdateCarsRequest updateCarsRequests, Car existingCar) {
        CarStatus updatedCarStatus = getUpdatedCarStatus(updateCarsRequests, existingCar);

        return carMapper.getUpdatedCarWithStatus(existingCar, updatedCarStatus);
    }

    private CarStatus getUpdatedCarStatus(UpdateCarsRequest updateCarsRequest, Car existingCar) {
        return existingCar.getId().toString().equals(updateCarsRequest.previousCarId()) ?
                CarStatus.AVAILABLE :
                CarStatus.NOT_AVAILABLE;
    }

    private List<ObjectId> getCarIds(UpdateCarsRequest updateCarsRequests) {
        return List.of(
                MongoUtil.getObjectId(updateCarsRequests.previousCarId()),
                MongoUtil.getObjectId(updateCarsRequests.actualCarId())
        );
    }

    private Mono<Car> setupUpdatedCar(String id, CarRequest updatedCarRequest) {
        return Mono.zip(
                List.of(
                        findEntityById(id),
                        branchService.findEntityById(updatedCarRequest.originalBranchId()),
                        branchService.findEntityById(updatedCarRequest.actualBranchId())
                ),
                carDetails -> getUpdatedCar(updatedCarRequest, carDetails)
        );
    }

    private Car getUpdatedCar(CarRequest updatedCarRequest, Object[] carDetails) {
        Car existingCar = (Car) carDetails[0];
        Branch originalBranch = (Branch) carDetails[1];
        Branch actualBranch = (Branch) carDetails[2];

        return carMapper.getUpdatedCar(existingCar.getId(), updatedCarRequest, originalBranch, actualBranch);
    }

    private Mono<Car> updateCarAfterClosingBooking(CarUpdateDetails carUpdateDetails) {
        return Mono.zip(
                findCarInUse(carUpdateDetails.carId()),
                employeeService.findEntityById(carUpdateDetails.receptionistEmployeeId()),
                (existingCar, receptionistEmployee) -> updateCarDetails(carUpdateDetails, existingCar, receptionistEmployee)
        );
    }

    private Car updateCarDetails(CarUpdateDetails carUpdateDetails, Car car, Employee employee) {
        CarState carState = carUpdateDetails.carState();
        CarStatus carStatus = CarStatus.valueOf(carState.name());

        return carMapper.getCarAfterBookingClosing(car, employee.getWorkingBranch(), carStatus);
    }

    private Mono<ObjectId> saveCarImage(Flux<DataBuffer> dataBufferFlux, String carId) {
        return reactiveGridFsTemplate.store(dataBufferFlux, carId);
    }

    private Flux<DataBuffer> getDataBuffer(Part part) {
        return ObjectUtils.isEmpty(part) ? Flux.empty() : part.content();
    }

    private Flux<ExcelCarRequest> extractCarsFromExcelRows(DataBuffer dataBuffer) {
        return Mono.fromCallable(() -> excelParserService.extractDataFromExcel(dataBuffer.asInputStream()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<Car> createNewCarFromExcelData(ExcelCarRequest excelCarRequest) {
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

    private AutoHubResponseStatusException getCarNotAvailableException() {
        return new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Selected car is not available");
    }

}
