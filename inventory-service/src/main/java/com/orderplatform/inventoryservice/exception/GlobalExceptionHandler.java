package com.orderplatform.inventoryservice.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleInventoryNotFound(
            InventoryNotFoundException exception) {

        return Map.of(
                "error", "INVENTORY_NOT_FOUND",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(InsufficientInventoryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleInsufficientInventory(
            InsufficientInventoryException exception) {

        return Map.of(
                "error", "INSUFFICIENT_INVENTORY",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgument(
            IllegalArgumentException exception) {

        return Map.of(
                "error", "BAD_REQUEST",
                "message", exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(
      MethodArgumentNotValidException exception) {

        String message = exception.getBindingResult()
          .getFieldErrors()
          .stream()
          .findFirst()
          .map(error -> error.getField() + ": " + error.getDefaultMessage())
          .orElse("Invalid request");

        return Map.of(
          "error", "VALIDATION_ERROR",
          "message", message
        );
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolation(
      DataIntegrityViolationException exception) {
        return Map.of(
          "error", "INVENTORY_ALREADY_EXISTS",
          "message", "Inventory already exists for this product"
        );
    }
}
