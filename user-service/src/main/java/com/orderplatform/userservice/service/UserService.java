package com.orderplatform.userservice.service;

import com.orderplatform.userservice.dto.UserRequest;
import com.orderplatform.userservice.dto.UserResponse;
import com.orderplatform.userservice.entity.User;
import com.orderplatform.userservice.exception.UserNotFoundException;
import com.orderplatform.userservice.mapper.UserMapper;
import com.orderplatform.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.orderplatform.userservice.exception.DuplicateEmailException;

import java.util.List;

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

  public UserResponse getUserById(Long id) {

    User user = userRepository.findById(id)
      .orElseThrow(() -> new UserNotFoundException("User not found"));

    return UserMapper.toResponse(user);
  }

  public List<UserResponse> getAllUsers() {
    return userRepository.findAll()
      .stream()
      .map(UserMapper::toResponse)
      .toList();
  }

  public UserResponse updateUser(Long id, UserRequest request) {

    User user = userRepository.findById(id)
      .orElseThrow(() -> new UserNotFoundException("User not found"));

    if (!user.getEmail().equals(request.getEmail())
      && userRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DuplicateEmailException("Email already exists");
    }

    user.setName(request.getName());
    user.setEmail(request.getEmail());

    User updatedUser = userRepository.save(user);

    return UserMapper.toResponse(updatedUser);
  }


  public void deleteUser(Long id) {

    User user = userRepository.findById(id)
      .orElseThrow(() -> new UserNotFoundException("User not found"));

    userRepository.delete(user);
  }
}
