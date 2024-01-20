package com.carrental.agency.service;

import com.carrental.agency.mapper.CarMapper;
import com.carrental.agency.repository.CarRepository;
import com.carrental.document.model.Branch;
import com.carrental.document.model.Car;
import com.carrental.document.model.CarFields;
import com.carrental.document.model.CarStatus;
import com.carrental.document.model.Employee;
import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
import com.carrental.lib.exceptionhandling.CarRentalException;
import com.carrental.lib.exceptionhandling.CarRentalResponseStatusException;
import com.carrental.lib.util.MongoUtil;
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
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {

    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final CarMapper carMapper;

    public Flux<CarDto> findAllCars() {
        return carRepository.findAll()
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding all cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<CarDto> findCarById(String id) {
        return findEntityById(id)
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding car by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<CarDto> findCarsByMake(String make) {
        return carRepository.findCarsByMake(make)
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while finding cars by make: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<CarDto> getAvailableCar(String id) {
        return findEntityById(id)
                .filter(car -> CarStatus.AVAILABLE.equals(car.getCarStatus()))
                .map(carMapper::mapEntityToDto)
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.BAD_REQUEST,
                                        "Selected car is not available"
                                )
                        )
                )
                .onErrorResume(e -> {
                    log.error("Error while getting available car: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<CarDto> findCarsByFilterInsensitiveCase(String filter) {
        return carRepository.findAllByFilterInsensitiveCase(filter)
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while searching car by criteria: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                })
                .switchIfEmpty(Mono.error(new CarRentalResponseStatusException(HttpStatus.NOT_FOUND, "No result")));
    }

    public Mono<CarDto> saveCar(CarDto carDto) {
        return Mono.just(carDto)
                .flatMap(this::getBranches)
                .flatMap(originalBranchAndActualBranch -> {
                    Car newCar = carMapper.mapDtoToEntity(carDto);
                    newCar.setOriginalBranch(originalBranchAndActualBranch.getT1());
                    newCar.setActualBranch(originalBranchAndActualBranch.getT2());

                    return carRepository.save(newCar);
                })
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while saving car: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Flux<CarDto> uploadCars(FilePart filePart) {
        return filePart.content()
                .concatMap(this::getDataFromExcelAsPublisher)
                .concatMap(carDto -> getBranches(carDto)
                        .map(originalBranchActualBranchTuple -> generateCar(carDto, originalBranchActualBranchTuple)))
                .collectList()
                .flatMapMany(carRepository::saveAll)
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while uploading cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<CarDto> updateCar(String id, CarDto updatedCarDto) {
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
                .onErrorResume(e -> {
                    log.error("Error while updating cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<CarDto> updateCarStatus(String id, CarStatusEnum carStatusEnum) {
        return findEntityById(id)
                .flatMap(car -> {
                    car.setCarStatus(carMapper.mapToCarStatus(carStatusEnum));

                    return carRepository.save(car);
                })
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while updating car status cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Void> deleteCarById(String id) {
        return carRepository.deleteById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while deleting cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    public Mono<Long> countCars() {
        return carRepository.count()
                .onErrorResume(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });

    }

    public Mono<CarDto> updateCarWhenBookingIsClosed(String id, CarDetailsForUpdateDto carDetailsForUpdateDto) {
        return findEntityById(id)
                .zipWith(employeeService.findEntityById(carDetailsForUpdateDto.getReceptionistEmployeeId()))
                .map(carEmployee -> updateCarDetails(carDetailsForUpdateDto, carEmployee))
                .flatMap(carRepository::save)
                .map(carMapper::mapEntityToDto)
                .onErrorResume(e -> {
                    log.error("Error while counting cars: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                });
    }

    private Mono<Car> findEntityById(String id) {
        return carRepository.findById(MongoUtil.getObjectId(id))
                .onErrorResume(e -> {
                    log.error("Error while finding by id: {}", e.getMessage());

                    return Mono.error(new CarRentalException(e.getMessage()));
                })
                .switchIfEmpty(
                        Mono.error(
                                new CarRentalResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Car with id " + id + " does not exist"
                                )
                        )
                );
    }

    private Mono<Tuple2<Branch, Branch>> getBranches(CarDto carDto) {
        return branchService.findEntityById(carDto.getOriginalBranchId())
                .zipWith(branchService.findEntityById(carDto.getActualBranchId()));
    }

    private Car updateExistingCar(CarDto updatedCarDto, Car existingCar, Tuple2<Branch, Branch> originalBranchAndActualBranch) {
        existingCar.setMake(updatedCarDto.getMake());
        existingCar.setModel(updatedCarDto.getModel());
        existingCar.setBodyType(carMapper.mapToBodyType(Optional.ofNullable(updatedCarDto.getBodyType()).orElseThrow()));
        existingCar.setYearOfProduction(Optional.ofNullable(updatedCarDto.getYearOfProduction()).orElseThrow());
        existingCar.setColor(updatedCarDto.getColor());
        existingCar.setMileage(Optional.ofNullable(updatedCarDto.getMileage()).orElseThrow());
        existingCar.setAmount(carMapper.mapBigDecimalToDouble(Optional.ofNullable(updatedCarDto.getAmount()).orElseThrow()));
        existingCar.setCarStatus(carMapper.mapToCarStatus(Optional.ofNullable(updatedCarDto.getCarStatus()).orElseThrow()));
        existingCar.setOriginalBranch(originalBranchAndActualBranch.getT1());
        existingCar.setActualBranch(originalBranchAndActualBranch.getT2());
        existingCar.setUrlOfImage(updatedCarDto.getUrlOfImage());

        return existingCar;
    }

    private Car updateCarDetails(CarDetailsForUpdateDto carDetailsForUpdateDto, Tuple2<Car, Employee> carEmployee) {
        Car car = carEmployee.getT1();
        CarStatusEnum carStatus = Optional.ofNullable(carDetailsForUpdateDto.getCarStatus())
                .orElseThrow();

        car.setActualBranch(carEmployee.getT2().getWorkingBranch());
        car.setCarStatus(carMapper.mapToCarStatus(carStatus));

        return car;
    }

    private Flux<CarDto> getDataFromExcelAsPublisher(DataBuffer dataBuffer) {
        return Mono.fromCallable(() -> extractDataFromExcel(dataBuffer.asInputStream()))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(Flux::fromIterable);
    }

    private List<CarDto> extractDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);

            return getValuesFromSheet(sheet);
        } catch (Exception e) {
            throw new CarRentalException(e);
        }
    }

    private List<CarDto> getValuesFromSheet(Sheet sheet) {
        DataFormatter dataFormatter = new DataFormatter();
        List<CarDto> carDtoList = new ArrayList<>();

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

            carDtoList.add(generateCarDto(values));
        }

        return carDtoList;
    }

    private Car generateCar(CarDto carDto, Tuple2<Branch, Branch> originalBranchActualBranchTuple) {
        Car car = carMapper.mapDtoToEntity(carDto);
        car.setOriginalBranch(originalBranchActualBranchTuple.getT1());
        car.setActualBranch(originalBranchActualBranchTuple.getT2());

        return car;
    }

    private CarDto generateCarDto(List<Object> values) {
        return new CarDto()
                .make((String) values.get(CarFields.MAKE.ordinal()))
                .model((String) values.get(CarFields.MODEL.ordinal()))
                .bodyType(CarDto.BodyTypeEnum.valueOf(((String) values.get(CarFields.BODY_TYPE.ordinal())).toUpperCase()))
                .yearOfProduction(Integer.parseInt((String) values.get(CarFields.YEAR_OF_PRODUCTION.ordinal())))
                .color((String) values.get(CarFields.COLOR.ordinal()))
                .mileage(Integer.parseInt((String) values.get(CarFields.MILEAGE.ordinal())))
                .carStatus(CarStatusEnum.valueOf(((String) values.get(CarFields.CAR_STATUS.ordinal())).toUpperCase()))
                .amount(BigDecimal.valueOf(Double.parseDouble((String) values.get(CarFields.AMOUNT.ordinal()))))
                .originalBranchId((String) values.get(CarFields.ORIGINAL_BRANCH.ordinal()))
                .actualBranchId((String) values.get(CarFields.ACTUAL_BRANCH.ordinal()))
                .urlOfImage((String) values.get(CarFields.URL_OF_IMAGE.ordinal()));
    }

}
