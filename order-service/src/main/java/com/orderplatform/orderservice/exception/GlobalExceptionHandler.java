package com.orderplatform.orderservice.exception;

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

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(MethodArgumentNotValidException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.BAD_REQUEST,
      "Validation failed for request"
    );
    problem.setTitle("Validation Error");
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
      fieldErrors.put(error.getField(), error.getDefaultMessage())
    );
    problem.setProperty("errors", fieldErrors);
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ProblemDetail handleOrderNotFound(OrderNotFoundException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
    problem.setTitle("Order Not Found");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ProblemDetail handleProductNotFound(ProductNotFoundException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
    problem.setTitle("Product Not Found");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ProblemDetail handleUserNotFound(UserNotFoundException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      exception.getMessage()
    );
    problem.setTitle("User Not Found");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ProblemDetail handleInsufficientStock(InsufficientStockException exception) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.CONFLICT,
      exception.getMessage()
    );
    problem.setTitle("Insufficient Stock");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }
}