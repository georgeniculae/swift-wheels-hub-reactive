package com.carrental.customer.mapper;

import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.model.Role;
import com.swiftwheelshub.model.User;
import com.carrental.dto.UserDto;
import org.apache.commons.lang3.ObjectUtils;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CustomerMapper {

    CurrentUserDto mapUserToCurrentUserDto(User user);

    UserDto mapEntityToDto(User user);

    default String mapObjectIdToString(ObjectId id) {
        return ObjectUtils.isEmpty(id) ? null : id.toString();
    }

    default UserDto.RoleEnum mapToRoleEnum(Role role) {
        return switch (role) {
            case ROLE_ADMIN -> UserDto.RoleEnum.ADMIN;
            case ROLE_USER -> UserDto.RoleEnum.USER;
            case ROLE_SUPPORT -> UserDto.RoleEnum.SUPPORT;
        };
    }

}
