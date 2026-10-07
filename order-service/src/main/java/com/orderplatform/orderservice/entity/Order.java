
  package com.orderplatform.orderservice.entity;

import com.orderplatform.orderservice.exception.InvalidOrderStateException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  private BigDecimal totalAmount;

  @Enumerated(EnumType.STRING)
  private OrderStatus status = OrderStatus.CREATED;

  private LocalDateTime createdAt;

  @OneToMany(
    mappedBy = "order",
    cascade = CascadeType.ALL,
    orphanRemoval = true
  )
  private List<OrderItem> items = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();

    if (status == null) {
      status = OrderStatus.CREATED;
    }
  }

  public void transitionTo(OrderStatus newStatus) {
    if (newStatus == null) {
      throw new IllegalArgumentException("Target order status cannot be null");
    }

    if (this.status == null) {
      this.status = OrderStatus.CREATED;
    }

    if (!this.status.canTransitionTo(newStatus)) {
      throw new InvalidOrderStateException(
        String.format(
          "Cannot transition order from %s to %s",
          this.status,
          newStatus
        )
      );
    }

    this.status = newStatus;
  }

  public void cancel() {
    transitionTo(OrderStatus.CANCELLED);
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public void setItems(List<OrderItem> items) {
    this.items = items;
  }
}