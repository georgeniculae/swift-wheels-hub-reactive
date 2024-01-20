package com.swiftwheelshub.agency.util;

import com.swiftwheelshub.model.BodyType;
import com.swiftwheelshub.model.Branch;
import com.swiftwheelshub.model.Car;
import com.swiftwheelshub.model.CarStatus;
import com.swiftwheelshub.model.Employee;
import com.swiftwheelshub.model.RentalOffice;
import com.carrental.dto.BranchDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
import com.carrental.dto.EmployeeDto;
import com.carrental.dto.RentalOfficeDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.getMake());
        assertEquals(car.getModel(), carDto.getModel());
        assertBodyType(car.getBodyType(), Optional.ofNullable(carDto.getBodyType()).orElseThrow());
        assertEquals(car.getYearOfProduction(), carDto.getYearOfProduction());
        assertEquals(car.getColor(), carDto.getColor());
        assertEquals(car.getMileage(), carDto.getMileage());
        assertCarStatus(car.getCarStatus(), Optional.ofNullable(carDto.getCarStatus()).orElseThrow());
        assertEquals(car.getAmount(), Optional.ofNullable(carDto.getAmount()).orElseThrow().doubleValue());
        assertEquals(car.getUrlOfImage(), carDto.getUrlOfImage());
    }

    public static void assertBranch(Branch branch, BranchDto branchDto) {
        assertEquals(branch.getName(), branchDto.getName());
        assertEquals(branch.getAddress(), branchDto.getAddress());
    }

    public static void assertRentalOffice(RentalOffice rentalOffice, RentalOfficeDto rentalOfficeDto) {
        assertEquals(rentalOffice.getName(), rentalOfficeDto.getName());
        assertEquals(rentalOffice.getContactAddress(), rentalOfficeDto.getContactAddress());
        assertEquals(rentalOffice.getLogoType(), rentalOfficeDto.getLogoType());
    }

    public static void assertEmployee(Employee employee, EmployeeDto employeeDto) {
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
        assertEquals(employee.getLastName(), employeeDto.getLastName());
        assertEquals(employee.getJobPosition(), employeeDto.getJobPosition());
        assertEquals(employee.getFirstName(), employeeDto.getFirstName());
    }

    private static void assertBodyType(BodyType bodyType, CarDto.BodyTypeEnum bodyTypeEnum) {
        assertEquals(bodyType.getDisplayName(), bodyTypeEnum.getValue());
    }

    private static void assertCarStatus(CarStatus carStatus, CarStatusEnum carStatusEnum) {
        assertEquals(carStatus.getDisplayName(), carStatusEnum.getValue());
    }

}
