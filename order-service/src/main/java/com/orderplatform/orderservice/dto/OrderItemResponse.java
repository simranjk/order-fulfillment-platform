package com.orderplatform.orderservice.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

  private Long productId;
  private Integer quantity;
  private BigDecimal price;

  public OrderItemResponse(Long productId,
                           Integer quantity,
                           BigDecimal price) {
    this.productId = productId;
    this.quantity = quantity;
    this.price = price;
  }

  public Long getProductId() {
    return productId;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public BigDecimal getPrice() {
    return price;
  }
}