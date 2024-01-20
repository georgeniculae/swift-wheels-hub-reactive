package com.carrental.agency.service;

import com.carrental.agency.mapper.CarMapper;
import com.carrental.agency.mapper.CarMapperImpl;
import com.carrental.agency.repository.CarRepository;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.Branch;
import com.carrental.document.model.Car;
import com.carrental.document.model.CarStatus;
import com.carrental.document.model.Employee;
import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findAll()).thenReturn(Flux.fromIterable(cars));

        StepVerifier.create(carService.findAllCars())
                .expectNext(carDto)
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
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.findCarById("64f361caf291ae086e179547"))
                .expectNext(carDto)
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
    void findCarsByMakeTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        List<Car> cars = List.of(car);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findCarsByMake(anyString())).thenReturn(Flux.fromIterable(cars));

        StepVerifier.create(carService.findCarsByMake("Volkswagen"))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void findCarsByMakeTest_errorOnFindingByMake() {
        when(carRepository.findCarsByMake(anyString())).thenReturn(Flux.error(new Throwable()));

        StepVerifier.create(carService.findCarsByMake("Volkswagen"))
                .expectError()
                .verify();
    }

    @Test
    void getAvailableCarTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.getAvailableCar("64f361caf291ae086e179547"))
                .expectNext(carDto)
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
    void findCarByFilterTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findAllByFilterInsensitiveCase(anyString())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.findCarsByFilterInsensitiveCase("search"))
                .expectNext(carDto)
                .verifyComplete();
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
    void saveCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.saveCar(carDto))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void saveCarTest_errorOnSaving() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.saveCar(carDto))
                .expectError()
                .verify();
    }

    @Test
    void uploadCarsTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/UploadedCar.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/UploadedCarDto.json", CarDto.class);

        Path path = Paths.get("src/test/resources/file/Cars.xlsx");
        Flux<DataBuffer> dataBuffer = DataBufferUtils.read(path, new DefaultDataBufferFactory(), 16384);

        when(filePart.content()).thenReturn(dataBuffer);
        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.saveAll(anyList())).thenReturn(Flux.just(car));

        StepVerifier.create(carService.uploadCars(filePart))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void updateCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCar("64f361caf291ae086e179547", carDto))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void updateCarStatusTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCarStatus("64f361caf291ae086e179547", CarStatusEnum.AVAILABLE))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void updateCarStatusTest_errorOnSave() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCarStatus("64f361caf291ae086e179547", CarStatusEnum.AVAILABLE))
                .expectError()
                .verify();
    }

    @Test
    void updateCarTest_errorOnSaving() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyString())).thenReturn(Mono.just(branch));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCar("64f361caf291ae086e179547", carDto))
                .expectError()
                .verify();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        CarDetailsForUpdateDto carDetailsForUpdateDto =
                TestUtils.getResourceAsJson("/data/CarDetailsForUpdateDto.json", CarDetailsForUpdateDto.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.just(car));

        StepVerifier.create(carService.updateCarWhenBookingIsClosed("64f361caf291ae086e179547", carDetailsForUpdateDto))
                .expectNext(carDto)
                .verifyComplete();
    }

    @Test
    void updateCarWhenBookingIsClosedTest_errorOnSave() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDetailsForUpdateDto carDetailsForUpdateDto =
                TestUtils.getResourceAsJson("/data/CarDetailsForUpdateDto.json", CarDetailsForUpdateDto.class);

        when(employeeService.findEntityById(anyString())).thenReturn(Mono.just(employee));
        when(carRepository.findById(any(ObjectId.class))).thenReturn(Mono.just(car));
        when(carRepository.save(any(Car.class))).thenReturn(Mono.error(new Throwable()));

        StepVerifier.create(carService.updateCarWhenBookingIsClosed("64f361caf291ae086e179547", carDetailsForUpdateDto))
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
