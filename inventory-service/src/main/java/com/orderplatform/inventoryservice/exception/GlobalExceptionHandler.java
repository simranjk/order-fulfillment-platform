package com.orderplatform.inventoryservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(InventoryNotFoundException.class)
  public ProblemDetail handleInventoryNotFound(InventoryNotFoundException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
    problem.setTitle("Inventory Not Found");
    problem.setProperty("error", "INVENTORY_NOT_FOUND");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(InsufficientInventoryException.class)
  public ProblemDetail handleInsufficientInventory(InsufficientInventoryException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.CONFLICT,
      exception.getMessage()
    );
    problem.setTitle("Insufficient Inventory");
    problem.setProperty("error", "INSUFFICIENT_INVENTORY");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      exception.getMessage()
    );
    problem.setTitle("Bad Request");
    problem.setProperty("error", "BAD_REQUEST");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      "Validation failed for request"
    );
    problem.setTitle("Validation Error");
    Map<String, String> fieldErrors = new HashMap<>();
    exception.getBindingResult().getFieldErrors().forEach(error ->
      fieldErrors.put(error.getField(), error.getDefaultMessage())
    );
    problem.setProperty("errors", fieldErrors);
    problem.setProperty("error", "VALIDATION_ERROR");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.CONFLICT,
      "Inventory already exists for this product"
    );
    problem.setTitle("Inventory Already Exists");
    problem.setProperty("error", "INVENTORY_ALREADY_EXISTS");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }
}
