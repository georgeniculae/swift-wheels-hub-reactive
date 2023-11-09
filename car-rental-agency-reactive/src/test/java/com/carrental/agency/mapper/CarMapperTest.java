package com.carrental.agency.mapper;

import com.carrental.agency.util.AssertionUtils;
import com.carrental.agency.util.TestUtils;
import com.carrental.document.model.Car;
import com.carrental.dto.CarDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        CarDto carDto = assertDoesNotThrow(() -> carMapper.mapEntityToDto(car));

        assertNotNull(carDto);
        AssertionUtils.assertCar(car, carDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        CarDto carDto = assertDoesNotThrow(() -> carMapper.mapEntityToDto(null));

        assertNull(carDto);
    }

    @Test
    void mapDtoToEntityTest_success() {
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        Car car = assertDoesNotThrow(() -> carMapper.mapDtoToEntity(carDto));

        assertNotNull(car);
        AssertionUtils.assertCar(car, carDto);
    }

    @Test
    void mapDtoToEntityTest_null() {
        Car car = assertDoesNotThrow(() -> carMapper.mapDtoToEntity(null));

        assertNull(car);
    }

}
