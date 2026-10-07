package com.orderplatform.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.orderservice.dto.CreateOrderRequest;
import com.orderplatform.orderservice.dto.OrderResponse;
import com.orderplatform.orderservice.dto.UpdateOrderStatusRequest;
import com.orderplatform.orderservice.entity.OrderStatus;
import com.orderplatform.orderservice.exception.*;
import com.orderplatform.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OrderControllerTest {

  private MockMvc mockMvc;

  @Mock
  private OrderService orderService;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    OrderController controller = new OrderController(orderService);

    mockMvc = MockMvcBuilders
      .standaloneSetup(controller)
      .setControllerAdvice(new GlobalExceptionHandler())
      .build();

    objectMapper = new ObjectMapper();
  }

  @Test
  void createOrder_shouldReturn201() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CREATED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.createOrder(any(CreateOrderRequest.class)))
      .thenReturn(response);

    String requestBody = """
      {
          "userId": 4,
          "items": [
              {
                  "productId": 6,
                  "quantity": 1
              }
          ]
      }
      """;

    mockMvc.perform(post("/api/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.orderId").value(1))
      .andExpect(jsonPath("$.userId").value(4))
      .andExpect(jsonPath("$.totalAmount").value(1000))
      .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getOrderById_shouldReturn200() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CREATED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.getOrderById(1L))
      .thenReturn(response);

    mockMvc.perform(get("/api/v1/orders/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderId").value(1))
      .andExpect(jsonPath("$.userId").value(4))
      .andExpect(jsonPath("$.status").value("CREATED"));
  }

  @Test
  void getOrdersByUserId_shouldReturn200() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CREATED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.getOrdersByUserId(4L))
      .thenReturn(List.of(response));

    mockMvc.perform(get("/api/v1/orders/user/4"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].orderId").value(1))
      .andExpect(jsonPath("$[0].userId").value(4))
      .andExpect(jsonPath("$[0].status").value("CREATED"));
  }

  @Test
  void createOrder_withInvalidRequest_shouldReturn400() throws Exception {

    String requestBody = """
      {
          "userId": 0,
          "items": []
      }
      """;

    mockMvc.perform(post("/api/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }

  @Test
  void getOrderById_whenOrderNotFound_shouldReturn404() throws Exception {

    when(orderService.getOrderById(999L))
      .thenThrow(new OrderNotFoundException("Order not found: 999"));

    mockMvc.perform(get("/api/v1/orders/999"))
      .andExpect(status().isNotFound());
  }

  @Test
  void createOrder_whenUserNotFound_shouldReturn404() throws Exception {

    when(orderService.createOrder(any(CreateOrderRequest.class)))
      .thenThrow(new UserNotFoundException("User not found: 999"));

    String requestBody = """
      {
          "userId": 999,
          "items": [
              {
                  "productId": 6,
                  "quantity": 1
              }
          ]
      }
      """;

    mockMvc.perform(post("/api/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isNotFound());
  }

  @Test
  void createOrder_whenInsufficientStock_shouldReturn409() throws Exception {

    when(orderService.createOrder(any(CreateOrderRequest.class)))
      .thenThrow(new InsufficientStockException(6L));

    String requestBody = """
      {
          "userId": 4,
          "items": [
              {
                  "productId": 6,
                  "quantity": 100
              }
          ]
      }
      """;

    mockMvc.perform(post("/api/v1/orders")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isConflict());
  }

  @Test
  void cancelOrder_whenAllowed_shouldReturn200() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CANCELLED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.cancelOrder(1L))
      .thenReturn(response);

    mockMvc.perform(post("/api/v1/orders/1/cancel"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderId").value(1))
      .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void cancelOrderPatch_whenAllowed_shouldReturn200() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CANCELLED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.cancelOrder(1L))
      .thenReturn(response);

    mockMvc.perform(patch("/api/v1/orders/1/cancel"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderId").value(1))
      .andExpect(jsonPath("$.status").value("CANCELLED"));
  }

  @Test
  void cancelOrder_whenNotAllowed_shouldReturn409() throws Exception {

    when(orderService.cancelOrder(1L))
      .thenThrow(new InvalidOrderStateException("Cannot transition order from SHIPPED to CANCELLED"));

    mockMvc.perform(post("/api/v1/orders/1/cancel"))
      .andExpect(status().isConflict());
  }

  @Test
  void cancelOrder_whenOrderNotFound_shouldReturn404() throws Exception {

    when(orderService.cancelOrder(999L))
      .thenThrow(new OrderNotFoundException("Order not found: 999"));

    mockMvc.perform(post("/api/v1/orders/999/cancel"))
      .andExpect(status().isNotFound());
  }

  @Test
  void updateOrderStatus_whenValidTransition_shouldReturn200() throws Exception {

    OrderResponse response = new OrderResponse(
      1L,
      4L,
      BigDecimal.valueOf(1000),
      OrderStatus.CONFIRMED,
      LocalDateTime.now(),
      Collections.emptyList()
    );

    when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED)))
      .thenReturn(response);

    String requestBody = """
      {
          "status": "CONFIRMED"
      }
      """;

    mockMvc.perform(patch("/api/v1/orders/1/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.orderId").value(1))
      .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }

  @Test
  void updateOrderStatus_whenInvalidTransition_shouldReturn409() throws Exception {

    when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.DELIVERED)))
      .thenThrow(new InvalidOrderStateException("Cannot transition order from CREATED to DELIVERED"));

    String requestBody = """
      {
          "status": "DELIVERED"
      }
      """;

    mockMvc.perform(patch("/api/v1/orders/1/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isConflict());
  }

  @Test
  void updateOrderStatus_whenOrderNotFound_shouldReturn404() throws Exception {

    when(orderService.updateOrderStatus(eq(999L), eq(OrderStatus.CONFIRMED)))
      .thenThrow(new OrderNotFoundException("Order not found: 999"));

    String requestBody = """
      {
          "status": "CONFIRMED"
      }
      """;

    mockMvc.perform(patch("/api/v1/orders/999/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isNotFound());
  }

  @Test
  void updateOrderStatus_withInvalidRequest_shouldReturn400() throws Exception {

    String requestBody = "{}";

    mockMvc.perform(patch("/api/v1/orders/1/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
      .andExpect(status().isBadRequest());
  }
}