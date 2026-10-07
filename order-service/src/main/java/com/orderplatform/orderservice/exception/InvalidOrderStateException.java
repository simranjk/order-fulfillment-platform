package com.orderplatform.orderservice.exception;

import com.orderplatform.orderservice.entity.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {

  public InvalidOrderStateException(String message) {
    super(message);
  }

  public InvalidOrderStateException(OrderStatus currentStatus, OrderStatus targetStatus) {
    super(String.format("Cannot transition order from %s to %s", currentStatus, targetStatus));
  }
}
