package com.orderplatform.orderservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleOrderNotFound(
    OrderNotFoundException exception) {

    return buildResponse(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleProductNotFound(
    ProductNotFoundException exception) {

    return buildResponse(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<Map<String, Object>> handleInsufficientStock(
    InsufficientStockException exception) {

    return buildResponse(
      HttpStatus.CONFLICT,
      exception.getMessage()
    );
  }

  private ResponseEntity<Map<String, Object>> buildResponse(
    HttpStatus status,
    String message) {

    Map<String, Object> body = new HashMap<>();

    body.put("timestamp", LocalDateTime.now());
    body.put("status", status.value());
    body.put("error", status.getReasonPhrase());
    body.put("message", message);

    return ResponseEntity.status(status).body(body);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleUserNotFound(
    UserNotFoundException exception) {

    return buildResponse(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
  }
}