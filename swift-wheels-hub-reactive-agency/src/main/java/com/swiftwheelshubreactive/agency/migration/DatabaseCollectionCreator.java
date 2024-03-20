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
        return List.of(
                createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222"),
                createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223")
        );
    }

    public static List<Branch> getBranches() {
        return List.of(
                createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222")),
                createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223"))
        );
    }

    public static List<Car> getCars() {
        return List.of(
                createCar("65072052d5d4531e66a0c00c", "Volkswagen", "Golf", BodyType.HATCHBACK, 2010, "black", 232000, BigDecimal.valueOf(500), createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222")), createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222"))),
                createCar("65072052d5d4531e66a0c00d", "Audi", "A4", BodyType.SEDAN, 2015, "white", 187000, BigDecimal.valueOf(800), createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223")), createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223")))
        );
    }

    public static List<Employee> getEmployees() {
        return List.of(
                createEmployee("Claudiu", "Alexandrescu", "manager", createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222"))),
                createEmployee("Marius", "Ionescu", "employee", createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "0722222222"))),
                createEmployee("Andrei", "Stefanescu", "manager", createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223"))),
                createEmployee("Alexandru", "Serbanescu", "employee", createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "0722222223")))
        );
    }

    private static Car createCar(String id, String make, String model, BodyType bodyType, int yearOfProduction,
                                 String color, int mileage, BigDecimal amount, Branch originalBranch, Branch actualBranch) {
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

    private static Branch createBranch(String id, String name, String address, RentalOffice rentalOffice) {
        return Branch.builder()
                .id(new ObjectId(id))
                .name(name)
                .address(address)
                .rentalOffice(rentalOffice)
                .build();
    }

    private static RentalOffice createRentalOffice(String id, String name, String contactAddress, String phoneNumber) {
        return RentalOffice.builder()
                .id(new ObjectId(id))
                .name(name)
                .contactAddress(contactAddress)
                .phoneNumber(phoneNumber)
                .build();
    }

    private static Employee createEmployee(String firstName, String lastName, String jobPosition, Branch branch) {
        return Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .jobPosition(jobPosition)
                .workingBranch(branch)
                .build();
    }

}
