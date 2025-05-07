package com.autohubreactive.agency.service;

import com.autohubreactive.agency.mapper.CarMapper;
import com.autohubreactive.agency.mapper.CarMapperImpl;
import com.autohubreactive.agency.repository.CarRepository;
import com.autohubreactive.agency.util.TestData;
import com.autohubreactive.agency.util.TestUtil;
import com.autohubreactive.agency.validator.CarRequestValidator;
import com.autohubreactive.dto.agency.CarRequest;
import com.autohubreactive.dto.agency.CarResponse;
import com.autohubreactive.dto.agency.ExcelCarRequest;
import com.autohubreactive.dto.common.AvailableCarInfo;
import com.autohubreactive.dto.common.CarStatusUpdate;
import com.autohubreactive.dto.common.CarUpdateDetails;
import com.autohubreactive.dto.common.UpdateCarsRequest;
import com.autohubreactive.exception.AutoHubException;
import com.autohubreactive.exception.AutoHubNotFoundException;
import com.autohubreactive.model.agency.Branch;
import com.autohubreactive.model.agency.Car;
import com.autohubreactive.model.agency.CarStatus;
import com.autohubreactive.model.agency.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BranchService branchService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private FilePart filePart;

    @Mock
    private CarRequestValidator carRequestValidator;

    @Mock
    private ExcelParserService excelParserService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ReactiveGridFsTemplate reactiveGridFsTemplate;

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAll()).thenReturn(Flux.fromIterable(cars));

        carService.findAllCars()
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();

        verify(carMapper).mapEntityToDto(any(Car.class));
    }

    @Test
    void findAllCarsTest_errorOnFindingAll() {
        when(carRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        carService.findAllCars()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findCarByIdTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        carService.findCarById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarByIdTest_errorOnFindingById() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        carService.findCarById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findCarByIdTest_notFound() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());

        carService.findCarById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError(AutoHubNotFoundException.class)
                .verify();
    }

    @Test
    void findCarsByMakeTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(cars));

        carService.findCarsByMakeInsensitiveCase("Volkswagen")
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarsByMakeTest_errorOnFindingByMake() {
        when(carRepository.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        carService.findCarsByMakeInsensitiveCase("Volkswagen")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void getAllAvailableCarTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAllAvailableCars()).thenReturn(Flux.just(car));

        carService.getAllAvailableCars()
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void getAllAvailableCarTest_errorOnFindingCars() {
        when(carRepository.findAllAvailableCars()).thenReturn(Flux.error(new RuntimeException()));

        carService.getAllAvailableCars()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void getAvailableCarTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        carService.getAvailableCar("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectNext(availableCarInfo)
                .verifyComplete();
    }

    @Test
    void getAvailableCarTest_noCarAvailable() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        car.setCarStatus(CarStatus.NOT_AVAILABLE);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        carService.getAvailableCar("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void getAvailableCarTest_errorOnFindingById() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        carService.getAvailableCar("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void findAllAvailableCarsTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAllAvailableCars()).thenReturn(Flux.just(car));

        carService.getAllAvailableCars()
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarByFilterTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(car));

        carService.findCarsByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarByFilterTest_errorOnFindingByFilter() {
        when(carRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        carService.findCarsByFilterInsensitiveCase("search")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void countCarsTest_success() {
        when(carRepository.count()).thenReturn(Mono.just(3L));

        carService.countCars()
                .as(StepVerifier::create)
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countCarsTest_errorOnCounting() {
        when(carRepository.count()).thenReturn(Mono.error(new Throwable()));

        carService.countCars()
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void saveCarTest_success() throws JsonProcessingException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));
        when(reactiveGridFsTemplate.store(any(), anyString()))
                .thenReturn(Mono.just(new ObjectId("65e8cf871b2c27702941b7a1")));

        carService.saveCar(multivalueMap.toSingleValueMap())
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void saveCarTest_noCarImage_success() throws JsonProcessingException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestWithoutCarImageMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        carService.saveCar(multivalueMap.toSingleValueMap())
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void saveCarTest_errorOnSaving() throws JsonProcessingException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        carService.saveCar(multivalueMap.toSingleValueMap())
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void uploadCarsTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        Car car = TestUtil.getResourceAsJson("/data/UploadedCar.json", Car.class);

        CarResponse carResponse =
                TestUtil.getResourceAsJson("/data/UploadedCarResponse.json", CarResponse.class);

        ExcelCarRequest excelCarRequest = TestData.getExcelCarRequest();

        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        when(filePart.content()).thenReturn(dataBuffer);
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(excelParserService.extractDataFromExcel(any(InputStream.class))).thenReturn(List.of(excelCarRequest));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));
        when(reactiveGridFsTemplate.store(any(), anyString()))
                .thenReturn(Mono.just(new ObjectId("65e8cf871b2c27702941b7a1")));

        carService.uploadCars(filePart)
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void uploadCarsTest_errorOnSavingCars() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        ExcelCarRequest excelCarRequest = TestData.getExcelCarRequest();

        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        when(filePart.content()).thenReturn(dataBuffer);
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(excelParserService.extractDataFromExcel(any(InputStream.class))).thenReturn(List.of(excelCarRequest));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new AutoHubException("error")));

        carService.uploadCars(filePart)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateCarTest_success() throws JsonProcessingException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtil.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));
        when(reactiveGridFsTemplate.store(any(), anyString()))
                .thenReturn(Mono.just(new ObjectId("65e8cf871b2c27702941b7a1")));

        carService.updateCar("64f361caf291ae086e179547", multivalueMap.toSingleValueMap())
                .as(StepVerifier::create)
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void updateCarTest_errorOnSaving() throws JsonProcessingException {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        carService.updateCar("64f361caf291ae086e179547", multivalueMap.toSingleValueMap())
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateCarStatusTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        carService.updateCarStatus(carStatusUpdate)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void updateCarStatusTest_errorOnSave() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        carService.updateCarStatus(carStatusUpdate)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateCarsStatusTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId("64f361caf291ae086e179547")
                .actualCarId("64f361caf291ae086e179222")
                .build();

        when(carRepository.findAllById(anyList())).thenReturn(Flux.just(car));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.just(car));

        carService.updateCarsStatus(updateCarsRequest)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void updateCarsStatusTest_errorOnSave() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId("64f361caf291ae086e179547")
                .actualCarId("64f361caf291ae086e179222")
                .build();

        when(carRepository.findAllById(anyList())).thenReturn(Flux.just(car));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.error(new RuntimeException()));

        carService.updateCarsStatus(updateCarsRequest)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findCarByIdAndCarStatus(any(ObjectId.class), any(CarStatus.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        carService.updateCarWhenBookingIsClosed(carUpdateDetails)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_noCarFound() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findCarByIdAndCarStatus(any(ObjectId.class), any(CarStatus.class))).thenReturn(Mono.empty());

        carService.updateCarWhenBookingIsClosed(carUpdateDetails)
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_errorOnSave() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarUpdateDetails carUpdateDetails =
                TestUtil.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findCarByIdAndCarStatus(any(ObjectId.class), any(CarStatus.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        carService.updateCarWhenBookingIsClosed(carUpdateDetails)
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

    @Test
    void deleteCarByIdTest_success() {
        when(carRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        carService.deleteCarById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectComplete()
                .verify();
    }

    @Test
    void deleteCarByIdTest_errorOnDeletingById() {
        when(carRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        carService.deleteCarById("64f361caf291ae086e179547")
                .as(StepVerifier::create)
                .expectError()
                .verify();
    }

}
