package com.swiftwheelshub.agency.mapper;

import com.carrental.dto.CarDto;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.model.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        CarDto carDto = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(car));

        assertNotNull(carDto);
        AssertionUtils.assertCar(car, carDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        CarDto carDto = Assertions.assertDoesNotThrow(() -> carMapper.mapEntityToDto(null));

        assertNull(carDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(carDto));

        assertNotNull(car);
        AssertionUtils.assertCar(car, carDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Car car = Assertions.assertDoesNotThrow(() -> carMapper.mapDtoToEntity(null));

        assertNull(car);
    }

}
