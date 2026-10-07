package com.orderplatform.productservice.exception;

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

  @ExceptionHandler(ProductNotFoundException.class)
  public ProblemDetail handleProductNotFound(ProductNotFoundException ex) {
    ProblemDetail problem = ProblemDetail.forStatusAndDetail(
      HttpStatus.NOT_FOUND,
      ex.getMessage()
    );
    problem.setTitle("Product Not Found");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }
}
