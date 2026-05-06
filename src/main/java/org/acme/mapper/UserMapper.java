package org.acme.mapper;

import org.acme.dto.response.UserResponse;
import org.acme.entity.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "cdi")
public interface UserMapper {
    UserResponse toUseResponse(User user);
}
