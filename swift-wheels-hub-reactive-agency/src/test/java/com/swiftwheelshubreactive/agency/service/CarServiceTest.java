package com.swiftwheelshubreactive.agency.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swiftwheelshubreactive.agency.mapper.CarMapper;
import com.swiftwheelshubreactive.agency.mapper.CarMapperImpl;
import com.swiftwheelshubreactive.agency.repository.CarRepository;
import com.swiftwheelshubreactive.agency.util.TestData;
import com.swiftwheelshubreactive.agency.util.TestUtils;
import com.swiftwheelshubreactive.agency.validator.CarRequestValidator;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.dto.CarState;
import com.swiftwheelshubreactive.dto.CarUpdateDetails;
import com.swiftwheelshubreactive.dto.ExcelCarRequest;
import com.swiftwheelshubreactive.dto.UpdateCarRequest;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubException;
import com.swiftwheelshubreactive.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import com.swiftwheelshubreactive.model.Employee;
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
    private ExcelProcessorService excelProcessorService;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAll()).thenReturn(Flux.fromIterable(cars));

        StepVerifier.create(carService.findAllCars())
                .expectNext(carResponse)
                .verifyComplete();

        verify(carMapper).mapEntityToDto(any(Car.class));
    }

    @Test
    void findAllCarsTest_errorOnFindingAll() {
        when(carRepository.findAll()).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(carService.findAllCars())
                .expectError()
                .verify();
    }

    @Test
    void findCarByIdTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.findCarById("64f361caf291ae086e179547"))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarByIdTest_errorOnFindingById() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.findCarById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void findCarByIdTest_notFound() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(carService.findCarById("64f361caf291ae086e179547"))
                .expectError(SwiftWheelsHubNotFoundException.class)
                .verify();
    }

    @Test
    void findCarsByMakeTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.fromIterable(cars));

        StepVerifier.create(carService.findCarsByMakeInsensitiveCase("Volkswagen"))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarsByMakeTest_errorOnFindingByMake() {
        when(carRepository.findCarsByMakeInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(carService.findCarsByMakeInsensitiveCase("Volkswagen"))
                .expectError()
                .verify();
    }

    @Test
    void getAvailableCarTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.getAvailableCar("64f361caf291ae086e179547"))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void getAvailableCarTest_noCarAvailable() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        car.setCarStatus(CarStatus.NOT_AVAILABLE);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.getAvailableCar("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void getAvailableCarTest_errorOnFindingById() {
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.getAvailableCar("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void findAllAvailableCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAllAvailableCars()).thenReturn(Flux.just(car));

        StepVerifier.create(carService.getAllAvailableCars())
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void findCarByFilterTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.findCarsByFilterInsensitiveCase("search"))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void getCarImageTest_success() {
        Car car = TestData.getCar();

        when(carRepository.findImageByCarId(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.getCarImage("64f361caf291ae086e179547"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getCarImageTest_errorOnFindingById() {
        when(carRepository.findImageByCarId(any(ObjectId.class))).thenReturn(Mono.error(new SwiftWheelsHubException("error")));

        StepVerifier.create(carService.getCarImage("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

    @Test
    void findCarByFilterTest_errorOnFindingByFilter() {
        when(carRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(carService.findCarsByFilterInsensitiveCase("search"))
                .expectError()
                .verify();
    }

    @Test
    void countCarsTest_success() {
        when(carRepository.count()).thenReturn(Mono.just(3L));

        StepVerifier.create(carService.countCars())
                .expectNext(3L)
                .verifyComplete();
    }

    @Test
    void countCarsTest_errorOnCounting() {
        when(carRepository.count()).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.countCars())
                .expectError()
                .verify();
    }

    @Test
    void saveCarTest_success() throws JsonProcessingException {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.saveCar(multivalueMap.toSingleValueMap()))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void saveCarTest_errorOnSaving() throws JsonProcessingException {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.saveCar(multivalueMap.toSingleValueMap()))
                .expectError()
                .verify();
    }

    @Test
    void uploadCarsTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        Car car = TestUtils.getResourceAsJson("/data/UploadedCar.json", Car.class);

        CarResponse carResponse =
                TestUtils.getResourceAsJson("/data/UploadedCarResponse.json", CarResponse.class);

        ExcelCarRequest excelCarRequest = TestData.getExcelCarRequest();

        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        when(filePart.content()).thenReturn(dataBuffer);
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(excelProcessorService.extractDataFromExcel(any(InputStream.class))).thenReturn(List.of(excelCarRequest));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.uploadCars(filePart))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void uploadCarsTest_errorOnSavingCars() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        ExcelCarRequest excelCarRequest = TestData.getExcelCarRequest();

        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 131072);

        when(filePart.content()).thenReturn(dataBuffer);
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(excelProcessorService.extractDataFromExcel(any(InputStream.class))).thenReturn(List.of(excelCarRequest));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.error(new SwiftWheelsHubException("error")));

        StepVerifier.create(carService.uploadCars(filePart))
                .expectError()
                .verify();
    }

    @Test
    void updateCarTest_success() throws JsonProcessingException {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCar("64f361caf291ae086e179547", multivalueMap.toSingleValueMap()))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void updateCarTest_errorOnSaving() throws JsonProcessingException {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        MultiValueMap<String, Part> multivalueMap = TestData.getCarRequestMultivalueMap();

        when(objectMapper.readValue(anyString(), eq(CarRequest.class))).thenReturn(carRequest);
        when(carRequestValidator.validateBody(any())).thenReturn(Mono.just(carRequest));
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCar("64f361caf291ae086e179547", multivalueMap.toSingleValueMap()))
                .expectError()
                .verify();
    }

    @Test
    void updateCarStatusTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCarStatus("64f361caf291ae086e179547", CarState.AVAILABLE))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void updateCarStatusTest_errorOnSave() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCarStatus("64f361caf291ae086e179547", CarState.AVAILABLE))
                .expectError()
                .verify();
    }

    @Test
    void updateCarsStatusTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        UpdateCarRequest updateCarRequest = UpdateCarRequest.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.AVAILABLE)
                .build();
        List<UpdateCarRequest> updateCarRequests = List.of(updateCarRequest);

        when(carRepository.findAllById(anyList())).thenReturn(Flux.just(car));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.updateCarsStatus(updateCarRequests))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void updateCarsStatusTest_errorOnSave() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        UpdateCarRequest updateCarRequest = UpdateCarRequest.builder()
                .carId("64f361caf291ae086e179547")
                .carState(CarState.AVAILABLE)
                .build();
        List<UpdateCarRequest> updateCarRequests = List.of(updateCarRequest);

        when(carRepository.findAllById(anyList())).thenReturn(Flux.just(car));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.error(new RuntimeException()));

        StepVerifier.create(carService.updateCarsStatus(updateCarRequests))
                .expectError()
                .verify();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarResponse carResponse = TestUtils.getResourceAsJson("/data/CarResponse.json", CarResponse.class);
        CarUpdateDetails carUpdateDetails =
                TestUtils.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCarWhenBookingIsClosed("64f361caf291ae086e179547", carUpdateDetails))
                .expectNext(carResponse)
                .verifyComplete();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_errorOnSave() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        CarUpdateDetails carUpdateDetails =
                TestUtils.getResourceAsJson("/data/CarUpdateDetails.json", CarUpdateDetails.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCarWhenBookingIsClosed("64f361caf291ae086e179547", carUpdateDetails))
                .expectError()
                .verify();
    }

    @Test
    void deleteCarByIdTest_success() {
        when(carRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.empty());

        StepVerifier.create(carService.deleteCarById("64f361caf291ae086e179547"))
                .expectComplete()
                .verify();
    }

    @Test
    void deleteCarByIdTest_errorOnDeletingById() {
        when(carRepository.deleteById(any(ObjectId.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.deleteCarById("64f361caf291ae086e179547"))
                .expectError()
                .verify();
    }

}
