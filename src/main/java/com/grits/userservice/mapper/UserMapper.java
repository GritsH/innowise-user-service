package com.grits.userservice.mapper;

import com.grits.userservice.entity.User;
import com.grits.userservice.model.request.user.CreateUserRequest;
import com.grits.userservice.model.request.user.UpdateUserRequest;
import com.grits.userservice.model.response.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserRequest request);

    void updateEntity(UpdateUserRequest request, @MappingTarget User user);

    UserResponse toResponse(User user);
}
