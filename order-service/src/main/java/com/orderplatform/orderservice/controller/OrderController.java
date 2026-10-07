package com.orderplatform.orderservice.controller;

import com.orderplatform.orderservice.dto.CreateOrderRequest;
import com.orderplatform.orderservice.dto.OrderResponse;
import com.orderplatform.orderservice.dto.UpdateOrderStatusRequest;
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

  @PostMapping("/{id}/cancel")
  public OrderResponse cancelOrder(@PathVariable Long id) {
    return orderService.cancelOrder(id);
  }

  @PatchMapping("/{id}/cancel")
  public OrderResponse cancelOrderPatch(@PathVariable Long id) {
    return orderService.cancelOrder(id);
  }

  @PatchMapping("/{id}/status")
  public OrderResponse updateOrderStatus(
    @PathVariable Long id,
    @Valid @RequestBody UpdateOrderStatusRequest request) {

    return orderService.updateOrderStatus(id, request.getStatus());
  }
}