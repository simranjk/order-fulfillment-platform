package com.orderplatform.userservice.mapper;

import com.orderplatform.userservice.dto.UserRequest;
import com.orderplatform.userservice.dto.UserResponse;
import com.orderplatform.userservice.entity.User;

public class UserMapper {

  public static User toEntity(UserRequest request) {
    return new User(request.getName(), request.getEmail());
  }

  public static UserResponse toResponse(User user) {
    return new UserResponse(
      user.getId(),
      user.getName(),
      user.getEmail()
    );
  }
}