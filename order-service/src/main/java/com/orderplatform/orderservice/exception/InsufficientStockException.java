package com.orderplatform.orderservice.exception;

public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(Long productId) {
    super("Insufficient stock for product: " + productId);
  }
}