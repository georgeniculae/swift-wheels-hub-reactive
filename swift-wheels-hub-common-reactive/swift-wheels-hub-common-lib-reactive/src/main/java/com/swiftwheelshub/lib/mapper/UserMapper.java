package com.swiftwheelshub.lib.mapper;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.model.Role;
import com.swiftwheelshub.model.User;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {

    UserDto mapEntityToDto(User user);

    User mapDtoToEntity(UserDto userDto);

    User mapRegisterRequestToUser(RegisterRequest registerRequest);

    default String mapObjectIdToString(ObjectId id) {
        return id.toString();
    }

    default ObjectId mapStringToObjectId(String id) {
        return new ObjectId(id);
    }

    default UserDto.RoleEnum mapToUserDtoRoleEnum(Role role) {
        return switch (role) {
            case ROLE_ADMIN -> UserDto.RoleEnum.ADMIN;
            case ROLE_USER -> UserDto.RoleEnum.USER;
            case ROLE_SUPPORT -> UserDto.RoleEnum.SUPPORT;
        };
    }

    default Role mapToUserRoleEnum(UserDto.RoleEnum role) {
        return switch (role) {
            case ADMIN -> Role.ROLE_ADMIN;
            case USER -> Role.ROLE_USER;
            case SUPPORT -> Role.ROLE_SUPPORT;
        };
    }

}
