package com.swiftwheelshubreactive.agency.service;

import com.swiftwheelshubreactive.agency.mapper.CarMapper;
import com.swiftwheelshubreactive.agency.repository.CarRepository;
import com.swiftwheelshubreactive.dto.BodyCategory;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshubreactive.lib.util.MongoUtil;
import com.swiftwheelshubreactive.model.BodyType;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarFields;
import com.swiftwheelshubreactive.model.CarStatus;
import com.swiftwheelshubreactive.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final CarMapper carMapper;

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

    public Mono<CarResponse> saveCar(CarRequest carRequest) {
        return getBranches(carRequest)
                .flatMap(originalBranchAndActualBranch -> {
                    Car newCar = carMapper.mapDtoToEntity(carRequest);
                    newCar.setOriginalBranch(originalBranchAndActualBranch.getT1());
                    newCar.setActualBranch(originalBranchAndActualBranch.getT2());

                    return carRepository.save(newCar);
                })
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

    public Mono<CarResponse> updateCar(String id, CarRequest updatedCarDto) {
        return findEntityById(id)
                .zipWith(getBranches(updatedCarDto))
                .flatMap(existingCarAndOriginalBranchActualBranchTuple -> {
                    Car existingCarUpdated = updateExistingCar(
                            updatedCarDto,
                            existingCarAndOriginalBranchActualBranchTuple.getT1(),
                            existingCarAndOriginalBranchActualBranchTuple.getT2()
                    );

                    return carRepository.save(existingCarUpdated);
                })
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

    private Mono<Tuple2<Branch, Branch>> getBranches(CarRequest carRequest) {
        return branchService.findEntityById(carRequest.originalBranchId())
                .zipWith(branchService.findEntityById(carRequest.actualBranchId()));
    }

    private Car updateExistingCar(CarRequest updatedCarRequest, Car existingCar, Tuple2<Branch, Branch> originalBranchAndActualBranch) {
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
        existingCar.setUrlOfImage(updatedCarRequest.urlOfImage());

        return existingCar;
    }

    private Car updateCarDetails(CarUpdateDetails carUpdateDetails, Tuple2<Car, Employee> carAndEmployee) {
        Car car = carAndEmployee.getT1();
        CarState carState = carUpdateDetails.carState();

        car.setActualBranch(carAndEmployee.getT2().getWorkingBranch());
        car.setCarStatus(CarStatus.valueOf(carState.name()));

        return car;
    }

    private Flux<CarRequest> getDataFromExcelAsPublisher(DataBuffer dataBuffer) {
        return Mono.fromCallable(() -> extractDataFromExcel(dataBuffer.asInputStream()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    private Mono<Car> createNewCar(CarRequest carRequest) {
        return getBranches(carRequest)
                .map(originalBranchActualBranchTuple -> generateCar(carRequest, originalBranchActualBranchTuple));
    }

    private List<CarRequest> extractDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            return getValuesFromSheet(sheet);
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }
    }

    private List<CarRequest> getValuesFromSheet(Sheet sheet) {
        DataFormatter dataFormatter = new DataFormatter();
        List<CarRequest> carRequests = new ArrayList<>();

        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            List<Object> values = new ArrayList<>();

            Row currentRow = sheet.getRow(index);
            Iterator<Cell> cellIterator = currentRow.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                    case STRING -> values.add(cell.getStringCellValue());
                    case NUMERIC -> values.add(dataFormatter.formatCellValue(cell));
                }
            }

            carRequests.add(generateCarRequest(values));
        }

        return Collections.unmodifiableList(carRequests);
    }

    private Car generateCar(CarRequest carRequest, Tuple2<Branch, Branch> originalBranchAndActualBranchTuple) {
        Car car = carMapper.mapDtoToEntity(carRequest);
        car.setOriginalBranch(originalBranchAndActualBranchTuple.getT1());
        car.setActualBranch(originalBranchAndActualBranchTuple.getT2());

        return car;
    }

    private CarRequest generateCarRequest(List<Object> values) {
        return CarRequest.builder()
                .make((String) values.get(CarFields.MAKE.ordinal()))
                .model((String) values.get(CarFields.MODEL.ordinal()))
                .bodyCategory(BodyCategory.valueOf(((String) values.get(CarFields.BODY_TYPE.ordinal())).toUpperCase()))
                .yearOfProduction(Integer.parseInt((String) values.get(CarFields.YEAR_OF_PRODUCTION.ordinal())))
                .color((String) values.get(CarFields.COLOR.ordinal()))
                .mileage(Integer.parseInt((String) values.get(CarFields.MILEAGE.ordinal())))
                .carState(CarState.valueOf(((String) values.get(CarFields.CAR_STATUS.ordinal())).toUpperCase()))
                .amount(new BigDecimal((String) values.get(CarFields.AMOUNT.ordinal())))
                .originalBranchId((String) values.get(CarFields.ORIGINAL_BRANCH.ordinal()))
                .actualBranchId((String) values.get(CarFields.ACTUAL_BRANCH.ordinal()))
                .urlOfImage((String) values.get(CarFields.URL_OF_IMAGE.ordinal()))
                .build();
    }

}
