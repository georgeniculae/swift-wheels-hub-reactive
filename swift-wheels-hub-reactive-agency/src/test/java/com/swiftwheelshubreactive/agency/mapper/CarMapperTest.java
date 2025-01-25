package com.swiftwheelshubreactive.agency.mapper;

import com.swiftwheelshubreactive.agency.util.AssertionUtil;
import com.swiftwheelshubreactive.agency.util.TestUtil;
import com.swiftwheelshubreactive.dto.CarRequest;
import com.swiftwheelshubreactive.dto.CarResponse;
import com.swiftwheelshubreactive.model.Car;
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
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarResponse carResponse = carMapper.mapEntityToDto(car);

        assertNotNull(carResponse);
        AssertionUtil.assertCarResponse(car, carResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(carMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        Car car = carMapper.mapDtoToEntity(carRequest);

        assertNotNull(car);
        AssertionUtil.assertCarRequest(car, carRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(carMapper.mapDtoToEntity(null));
    }

}
