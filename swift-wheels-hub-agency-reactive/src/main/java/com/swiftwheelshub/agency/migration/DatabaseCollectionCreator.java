package com.swiftwheelshub.agency.migration;

import com.carrental.document.model.BodyType;
import com.carrental.document.model.Branch;
import com.carrental.document.model.Car;
import com.carrental.document.model.CarStatus;
import com.carrental.document.model.Employee;
import com.carrental.document.model.RentalOffice;
import org.bson.types.ObjectId;

import java.util.List;

public class DatabaseCollectionCreator {

    public static List<RentalOffice> getRentalOffices() {
        return List.of(
                createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1"),
                createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2")
        );
    }

    public static List<Branch> getBranches() {
        return List.of(
                createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1")),
                createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2"))
        );
    }

    public static List<Car> getCars() {
        return List.of(
                createCar("65072052d5d4531e66a0c00c", "Volkswagen", "Golf", BodyType.HATCHBACK, 2010, "black", 232000, 500.0, createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1")), createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1")), "url1"),
                createCar("65072052d5d4531e66a0c00d", "Audi", "A4", BodyType.SEDAN, 2015, "white", 187000, 800.0, createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2")), createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2")), "url2")
        );
    }

    public static List<Employee> getEmployees() {
        return List.of(
                createEmployee("Claudiu", "Alexandrescu", "manager", createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1"))),
                createEmployee("Marius", "Ionescu", "employee", createBranch("65072051d5d4531e66a0c00a", "Branch 1", "Ploiesti", createRentalOffice("65072050d5d4531e66a0c008", "Rental Office 1", "contact address 1", "logo 1"))),
                createEmployee("Andrei", "Stefanescu", "manager", createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2"))),
                createEmployee("Alexandru", "Serbanescu", "employee", createBranch("65072051d5d4531e66a0c00b", "Branch 2", "Bucuresti", createRentalOffice("65072050d5d4531e66a0c009", "Rental Office 2", "contact address 2", "logo 2")))
        );
    }

    private static Car createCar(String id, String make, String model, BodyType bodyType, int yearOfProduction, String color,
                                 int mileage, Double amount, Branch originalBranch, Branch actualBranch, String urlOfImage) {
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
                .urlOfImage(urlOfImage)
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

    private static RentalOffice createRentalOffice(String id, String name, String contactAddress, String logoType) {
        return RentalOffice.builder()
                .id(new ObjectId(id))
                .name(name)
                .contactAddress(contactAddress)
                .logoType(logoType)
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
