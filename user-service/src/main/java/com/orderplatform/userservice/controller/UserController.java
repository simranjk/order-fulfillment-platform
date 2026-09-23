package com.orderplatform.userservice.controller;

import com.orderplatform.userservice.dto.UserRequest;
import com.orderplatform.userservice.dto.UserResponse;
import com.orderplatform.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UserResponse createUser(@Valid @RequestBody UserRequest request) {
    return userService.createUser(request);
  }

  @GetMapping("/{id}")
  public UserResponse getUserById(@PathVariable Long id) {
    return userService.getUserById(id);
  }

  @GetMapping
  public List<UserResponse> getAllUsers() {
    return userService.getAllUsers();
  }
}