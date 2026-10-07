package com.orderplatform.orderservice.entity;

import java.util.Set;

public enum OrderStatus {

  CREATED,
  CONFIRMED,
  PROCESSING,
  SHIPPED,
  DELIVERED,
  CANCELLED;

  public boolean canTransitionTo(OrderStatus target) {
    if (target == null) {
      return false;
    }
    return switch (this) {
      case CREATED -> target == CONFIRMED || target == CANCELLED;
      case CONFIRMED -> target == PROCESSING || target == CANCELLED;
      case PROCESSING -> target == SHIPPED;
      case SHIPPED -> target == DELIVERED;
      case DELIVERED, CANCELLED -> false;
    };
  }

  public boolean isCancellable() {
    return this == CREATED || this == CONFIRMED;
  }

  public Set<OrderStatus> nextValidStatuses() {
    return switch (this) {
      case CREATED -> Set.of(CONFIRMED, CANCELLED);
      case CONFIRMED -> Set.of(PROCESSING, CANCELLED);
      case PROCESSING -> Set.of(SHIPPED);
      case SHIPPED -> Set.of(DELIVERED);
      case DELIVERED, CANCELLED -> Set.of();
    };
  }
}