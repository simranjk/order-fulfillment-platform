package com.orderplatform.userservice.controller;

import com.orderplatform.userservice.exception.DuplicateEmailException;
import com.orderplatform.userservice.exception.UserNotFoundException;
import tools.jackson.databind.ObjectMapper;
import com.orderplatform.userservice.dto.UserRequest;
import com.orderplatform.userservice.dto.UserResponse;
import com.orderplatform.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  void createUser_shouldReturn201() throws Exception {

    UserRequest request = new UserRequest();
    request.setName("Sam");
    request.setEmail("sam@example.com");

    UserResponse response =
      new UserResponse(1L, "Sam", "sam@example.com");

    when(userService.createUser(any(UserRequest.class)))
      .thenReturn(response);

    mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated());
  }

  @Test
  void createUser_withInvalidRequest_shouldReturn400() throws Exception {

    UserRequest request = new UserRequest();
    request.setName("");
    request.setEmail("invalid-email");

    mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  void createUser_withDuplicateEmail_shouldReturn409() throws Exception {

    UserRequest request = new UserRequest();
    request.setName("Sam");
    request.setEmail("sam@example.com");

    when(userService.createUser(any(UserRequest.class)))
      .thenThrow(new DuplicateEmailException("Email already exists"));

    mockMvc.perform(
        post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isConflict());
  }

  @Test
  void getUser_shouldReturn200() throws Exception {

    UserResponse response =
      new UserResponse(1L, "Sam", "sam@example.com");

    when(userService.getUserById(1L))
      .thenReturn(response);

    mockMvc.perform(
        get("/api/v1/users/1")
      )
      .andExpect(status().isOk());
  }

  @Test
  void getUser_whenNotFound_shouldReturn404() throws Exception {

    when(userService.getUserById(999L))
      .thenThrow(new UserNotFoundException("User not found"));

    mockMvc.perform(
        get("/api/v1/users/999")
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void updateUser_shouldReturn200() throws Exception {

    UserRequest request = new UserRequest();
    request.setName("Sam Updated");
    request.setEmail("samupdated@example.com");

    UserResponse response =
      new UserResponse(1L, "Sam Updated", "samupdated@example.com");

    when(userService.updateUser(eq(1L), any(UserRequest.class)))
      .thenReturn(response);

    mockMvc.perform(
        put("/api/v1/users/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk());
  }
}