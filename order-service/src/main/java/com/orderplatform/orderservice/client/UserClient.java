package com.orderplatform.orderservice.client;

import com.orderplatform.orderservice.dto.UserResponse;
import com.orderplatform.orderservice.exception.UserNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class UserClient {

  private final RestClient restClient;

  public UserClient() {
    this.restClient = RestClient.builder()
      .baseUrl("http://localhost:8081")
      .build();
  }

  public UserResponse getUser(Long userId) {

    try {
      return restClient.get()
        .uri("/api/v1/users/{id}", userId)
        .retrieve()
        .body(UserResponse.class);

    } catch (RestClientResponseException e) {

      if (e.getStatusCode().value() == 404) {
        throw new UserNotFoundException(userId);
      }

      throw e;
    }
  }
}