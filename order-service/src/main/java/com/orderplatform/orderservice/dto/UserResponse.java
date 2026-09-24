package com.orderplatform.orderservice.dto;

public class UserResponse {

  private Long id;
  private String firstName;
  private String lastName;
  private String email;

  public UserResponse() {
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }
}