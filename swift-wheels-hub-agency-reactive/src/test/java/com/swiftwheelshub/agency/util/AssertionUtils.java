package com.swiftwheelshub.agency.util;

import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
import com.swiftwheelshub.model.Branch;
import com.swiftwheelshub.model.Car;
import com.swiftwheelshub.model.Employee;
import com.swiftwheelshub.model.RentalOffice;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCarRequest(Car car, CarRequest carRequest) {
        assertEquals(car.getMake(), carRequest.make());
        assertEquals(car.getModel(), carRequest.model());
        assertEquals(car.getBodyType().name(), carRequest.bodyCategory().name());
        assertEquals(car.getYearOfProduction(), carRequest.yearOfProduction());
        assertEquals(car.getColor(), carRequest.color());
        assertEquals(car.getMileage(), carRequest.mileage());
        assertEquals(car.getCarStatus().name(), carRequest.carState().name());
        assertEquals(car.getAmount(), carRequest.amount());
        assertEquals(car.getUrlOfImage(), carRequest.urlOfImage());
    }

    public static void assertCarResponse(Car car, CarResponse carResponse) {
        assertEquals(car.getMake(), carResponse.make());
        assertEquals(car.getModel(), carResponse.model());
        assertEquals(car.getBodyType().name(), carResponse.bodyCategory().name());
        assertEquals(car.getYearOfProduction(), carResponse.yearOfProduction());
        assertEquals(car.getColor(), carResponse.color());
        assertEquals(car.getMileage(), carResponse.mileage());
        assertEquals(car.getCarStatus().name(), carResponse.carState().name());
        assertEquals(car.getAmount(), carResponse.amount());
        assertEquals(car.getUrlOfImage(), carResponse.urlOfImage());
    }

    public static void assertBranchRequest(Branch branch, BranchRequest branchRequest) {
        assertEquals(branch.getName(), branchRequest.name());
        assertEquals(branch.getAddress(), branchRequest.address());
    }

    public static void assertBranchResponse(Branch branch, BranchResponse branchResponse) {
        assertEquals(branch.getName(), branchResponse.name());
        assertEquals(branch.getAddress(), branchResponse.address());
    }

    public static void assertRentalOfficeRequest(RentalOffice rentalOffice, RentalOfficeRequest rentalOfficeRequest) {
        assertEquals(rentalOffice.getName(), rentalOfficeRequest.name());
        assertEquals(rentalOffice.getContactAddress(), rentalOfficeRequest.contactAddress());
        assertEquals(rentalOffice.getLogoType(), rentalOfficeRequest.logoType());
    }

    public static void assertRentalOfficeResponse(RentalOffice rentalOffice, RentalOfficeResponse rentalOfficeResponse) {
        assertEquals(rentalOffice.getName(), rentalOfficeResponse.name());
        assertEquals(rentalOffice.getContactAddress(), rentalOfficeResponse.contactAddress());
        assertEquals(rentalOffice.getLogoType(), rentalOfficeResponse.logoType());
    }

    public static void assertEmployeeRequest(Employee employee, EmployeeRequest employeeRequest) {
        assertEquals(employee.getFirstName(), employeeRequest.firstName());
        assertEquals(employee.getLastName(), employeeRequest.lastName());
        assertEquals(employee.getJobPosition(), employeeRequest.jobPosition());
        assertEquals(employee.getFirstName(), employeeRequest.firstName());
    }

    public static void assertEmployeeResponse(Employee employee, EmployeeResponse employeeResponse) {
        assertEquals(employee.getFirstName(), employeeResponse.firstName());
        assertEquals(employee.getLastName(), employeeResponse.lastName());
        assertEquals(employee.getJobPosition(), employeeResponse.jobPosition());
        assertEquals(employee.getFirstName(), employeeResponse.firstName());
    }

}
