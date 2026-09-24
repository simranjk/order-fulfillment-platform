package com.orderplatform.orderservice.controller;

import com.orderplatform.orderservice.dto.CreateOrderRequest;
import com.orderplatform.orderservice.dto.OrderResponse;
import com.orderplatform.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

  private final OrderService orderService;

  public OrderController(OrderService orderService) {
    this.orderService = orderService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public OrderResponse createOrder(
    @Valid @RequestBody CreateOrderRequest request) {

    return orderService.createOrder(request);
  }

  @GetMapping("/{id}")
  public OrderResponse getOrderById(
    @PathVariable Long id) {

    return orderService.getOrderById(id);
  }

  @GetMapping("/user/{userId}")
  public List<OrderResponse> getOrdersByUserId(
    @PathVariable Long userId) {

    return orderService.getOrdersByUserId(userId);
  }
}


//This gives us three APIs:

//| Method | Endpoint                       | Purpose           |
//| ------ | ------------------------------ | ----------------- |
//| `POST` | `/api/v1/orders`               | Create order      |
//| `GET`  | `/api/v1/orders/{id}`          | Get one order     |
//| `GET`  | `/api/v1/orders/user/{userId}` | Get user's orders |