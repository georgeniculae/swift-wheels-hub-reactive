package com.swiftwheelshub.customer.migration;

import com.swiftwheelshub.model.Role;
import com.swiftwheelshub.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public class DatabaseCollectionCreator {

    public static List<User> getUsers() {
        return List.of(
                createUser("admin", "$2a$10$hadYmhDPuigFKchXrkmmUe6i1L8B50Be.ggbdVuszCbYu7yg14Lqa", Role.ROLE_ADMIN, "admin", "admin", "admin@email.com", LocalDate.of(1970, Month.JANUARY, 1), ""),
                createUser("user", "$2a$10$TxvQ5OD.ZEK3IEjMS7JFyuFyjm1X8CSaAPGCF/daFahfHXccdKkqO", Role.ROLE_USER, "user", "user", "user@email.com", LocalDate.of(1970, Month.JANUARY, 1), ""),
                createUser("support", "$2a$10$bje5mA18dGDa4N/Ds/eQGuBm3anOkjSgn81f6VojjwEGb74VNZpfG", Role.ROLE_SUPPORT, "support", "support", "support@email.com", LocalDate.of(1970, Month.JANUARY, 1), ""),
                createUser("ionpopescu", "$2a$12$ixPC0.X/n6gko36V356aS.CR0EOrPpRHv7Ez7jEoVnT.AnP3oa7xm", Role.ROLE_USER, "Ion", "Popescu", "ion.popescu@email.com", LocalDate.of(1985, Month.APRIL, 15), "Ploiesti")
        );
    }

    private static User createUser(String username, String password, Role role, String firstName, String lastName,
                                   String email, LocalDate dateOfBirth, String address) {
        return User.builder()
                .username(username)
                .password(password)
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .dateOfBirth(dateOfBirth)
                .address(address)
                .build();
    }

}
