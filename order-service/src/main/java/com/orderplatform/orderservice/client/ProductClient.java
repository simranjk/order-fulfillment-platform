package com.orderplatform.orderservice.client;

import com.orderplatform.orderservice.dto.ProductResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

  private final RestClient restClient;

  public ProductClient() {
    this.restClient = RestClient.builder()
      .baseUrl("http://localhost:8082")
      .build();
  }

  public ProductResponse getProduct(Long productId) {

    return restClient.get()
      .uri("/api/v1/products/{id}", productId)
      .retrieve()
      .body(ProductResponse.class);
  }
}