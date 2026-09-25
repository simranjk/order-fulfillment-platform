package com.orderplatform.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderplatform.orderservice.dto.CreateOrderRequest;
import com.orderplatform.orderservice.dto.OrderResponse;
import com.orderplatform.orderservice.entity.OrderStatus;
import com.orderplatform.orderservice.exception.GlobalExceptionHandler;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orderplatform.orderservice.exception.InsufficientStockException;
import com.orderplatform.orderservice.exception.OrderNotFoundException;
import com.orderplatform.orderservice.exception.ProductNotFoundException;
import com.orderplatform.orderservice.exception.UserNotFoundException;

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
      .thenThrow(new OrderNotFoundException("Order not found"));

    mockMvc.perform(get("/api/v1/orders/999"))
      .andExpect(status().isNotFound());
  }

  @Test
  void createOrder_whenUserNotFound_shouldReturn404() throws Exception {

    when(orderService.createOrder(any(CreateOrderRequest.class)))
      .thenThrow(new UserNotFoundException("User not found"));

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
}