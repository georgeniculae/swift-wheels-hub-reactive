package com.swiftwheelshubreactive.agency.migration;

import com.swiftwheelshubreactive.model.BodyType;
import com.swiftwheelshubreactive.model.Branch;
import com.swiftwheelshubreactive.model.Car;
import com.swiftwheelshubreactive.model.CarStatus;
import com.swiftwheelshubreactive.model.Employee;
import com.swiftwheelshubreactive.model.RentalOffice;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.List;

public class DatabaseCollectionCreator {

    public static List<RentalOffice> getRentalOffices() {
        return List.of(getRentalOffice1(), getRentalOffice2());
    }

    public static List<Branch> getBranches() {
        return List.of(getBranch1(), getBranch2());
    }

    public static List<Car> getCars() {
        return List.of(
                createCar(
                        "65072052d5d4531e66a0c00c",
                        "Volkswagen",
                        "Golf",
                        BodyType.HATCHBACK,
                        2010,
                        "black",
                        232000,
                        BigDecimal.valueOf(500),
                        getBranch1(),
                        getBranch1()
                ),
                createCar(
                        "65072052d5d4531e66a0c00d",
                        "Audi",
                        "A4",
                        BodyType.SEDAN,
                        2015,
                        "white",
                        187000,
                        BigDecimal.valueOf(800),
                        getBranch2(),
                        getBranch2()
                )
        );
    }

    public static List<Employee> getEmployees() {
        return List.of(
                createEmployee(
                        "66033bb2eeec155394c8311d",
                        "Claudiu",
                        "Alexandrescu",
                        "manager",
                        getBranch1()
                ),
                createEmployee(
                        "66033bb2eeec155394c8311e",
                        "Marius",
                        "Ionescu",
                        "employee",
                        getBranch1()
                ),
                createEmployee(
                        "66033bb2eeec155394c8311f",
                        "Andrei",
                        "Stefanescu",
                        "manager",
                        getBranch2()
                ),
                createEmployee(
                        "66033bb2eeec155394c83120",
                        "Alexandru",
                        "Serbanescu",
                        "employee",
                        getBranch2()
                )
        );
    }

    private static RentalOffice getRentalOffice1() {
        return createRentalOffice(
                "65072050d5d4531e66a0c008",
                "Rental Office 1",
                "contact address 1",
                "0722222222"
        );
    }

    private static RentalOffice getRentalOffice2() {
        return createRentalOffice(
                "65072050d5d4531e66a0c009",
                "Rental Office 2",
                "contact address 2",
                "0722222223"
        );
    }

    private static Branch getBranch1() {
        return createBranch(
                "65072051d5d4531e66a0c00a",
                "Branch 1",
                "Ploiesti",
                getRentalOffice1()
        );
    }

    private static Branch getBranch2() {
        return createBranch(
                "65072051d5d4531e66a0c00b",
                "Branch 2",
                "Bucuresti",
                getRentalOffice2()
        );
    }

    private static RentalOffice createRentalOffice(String id, String name, String contactAddress, String phoneNumber) {
        return RentalOffice.builder()
                .id(new ObjectId(id))
                .name(name)
                .contactAddress(contactAddress)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static Branch createBranch(String id, String name, String address, RentalOffice rentalOffice) {
        return Branch.builder()
                .id(new ObjectId(id))
                .name(name)
                .address(address)
                .rentalOffice(rentalOffice)
                .build();
    }

    private static Employee createEmployee(String id, String firstName, String lastName, String jobPosition,
                                           Branch branch) {
        return Employee.builder()
                .id(new ObjectId(id))
                .firstName(firstName)
                .lastName(lastName)
                .jobPosition(jobPosition)
                .workingBranch(branch)
                .build();
    }

    private static Car createCar(String id, String make, String model, BodyType bodyType, Integer yearOfProduction,
                                 String color, Integer mileage, BigDecimal amount, Branch originalBranch,
                                 Branch actualBranch) {
        return Car.builder()
                .id(new ObjectId(id))
                .make(make)
                .model(model)
                .bodyType(bodyType)
                .yearOfProduction(yearOfProduction)
                .color(color)
                .mileage(mileage)
                .carStatus(CarStatus.AVAILABLE)
                .amount(amount)
                .originalBranch(originalBranch)
                .actualBranch(actualBranch)
                .build();
    }

}
