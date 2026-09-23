package com.orderplatform.userservice.service;

import com.orderplatform.userservice.dto.UserRequest;
import com.orderplatform.userservice.dto.UserResponse;
import com.orderplatform.userservice.entity.User;
import com.orderplatform.userservice.mapper.UserMapper;
import com.orderplatform.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.orderplatform.userservice.exception.DuplicateEmailException;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserResponse createUser(UserRequest request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DuplicateEmailException("Email already exists");
    }

    User user = UserMapper.toEntity(request);

    User savedUser = userRepository.save(user);

    return UserMapper.toResponse(savedUser);
  }


}
