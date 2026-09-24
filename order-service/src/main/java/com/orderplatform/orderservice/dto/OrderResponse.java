package com.orderplatform.orderservice.dto;

import com.orderplatform.orderservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

  private Long orderId;
  private Long userId;
  private BigDecimal totalAmount;
  private OrderStatus status;
  private LocalDateTime createdAt;
  private List<OrderItemResponse> items;

  public OrderResponse(Long orderId,
                       Long userId,
                       BigDecimal totalAmount,
                       OrderStatus status,
                       LocalDateTime createdAt,
                       List<OrderItemResponse> items) {

    this.orderId = orderId;
    this.userId = userId;
    this.totalAmount = totalAmount;
    this.status = status;
    this.createdAt = createdAt;
    this.items = items;
  }

  public Long getOrderId() {
    return orderId;
  }

  public Long getUserId() {
    return userId;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<OrderItemResponse> getItems() {
    return items;
  }
}