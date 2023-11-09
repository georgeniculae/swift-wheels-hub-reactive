package com.carrental.document.dto;

import com.carrental.document.model.Role;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

public record CurrentUserDto(String id,
                             String username,
                             String password,
                             Role role,
                             String firstName,
                             String lastName,
                             String email,
                             String address,
                             LocalDate dateOfBirth,
                             Boolean credentialsNonExpired,
                             Boolean accountNonExpired,
                             Boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {

}
