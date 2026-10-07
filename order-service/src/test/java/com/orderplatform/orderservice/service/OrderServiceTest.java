
  package com.orderplatform.orderservice.service;

import com.orderplatform.orderservice.client.ProductClient;
import com.orderplatform.orderservice.client.UserClient;
import com.orderplatform.orderservice.dto.*;
import com.orderplatform.orderservice.entity.Order;
import com.orderplatform.orderservice.entity.OrderItem;
import com.orderplatform.orderservice.entity.OrderStatus;
import com.orderplatform.orderservice.exception.*;
import com.orderplatform.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private ProductClient productClient;

  @Mock
  private UserClient userClient;

  @InjectMocks
  private OrderService orderService;

  private Order existingOrder;

  @BeforeEach
  void setUp() {
    existingOrder = new Order();
    existingOrder.setUserId(1L);
    existingOrder.setTotalAmount(new BigDecimal("100.00"));

    OrderItem item = new OrderItem();
    item.setProductId(10L);
    item.setQuantity(2);
    item.setPrice(new BigDecimal("50.00"));
    item.setOrder(existingOrder);

    List<OrderItem> items = new ArrayList<>();
    items.add(item);
    existingOrder.setItems(items);
  }

  private void transitionOrderTo(Order order, OrderStatus targetStatus) {
    switch (targetStatus) {
      case CREATED -> {
        // New orders start in CREATED.
      }
      case CONFIRMED -> order.transitionTo(OrderStatus.CONFIRMED);
      case PROCESSING -> {
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.PROCESSING);
      }
      case SHIPPED -> {
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.PROCESSING);
        order.transitionTo(OrderStatus.SHIPPED);
      }
      case DELIVERED -> {
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.PROCESSING);
        order.transitionTo(OrderStatus.SHIPPED);
        order.transitionTo(OrderStatus.DELIVERED);
      }
      case CANCELLED -> order.transitionTo(OrderStatus.CANCELLED);
    }
  }

  @Test
  void createOrder_shouldSucceedWithInitialStatusCreated() {
    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);

    OrderItemRequest itemRequest = new OrderItemRequest();
    itemRequest.setProductId(10L);
    itemRequest.setQuantity(2);
    request.setItems(List.of(itemRequest));

    UserResponse userResponse =
      new UserResponse(1L, "Alice", "alice@example.com");

    when(userClient.getUser(1L)).thenReturn(userResponse);

    ProductResponse productResponse = new ProductResponse(
      10L,
      "Widget",
      "Widget desc",
      new BigDecimal("50.00"),
      10,
      true
    );

    when(productClient.getProduct(10L)).thenReturn(productResponse);

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(invocation -> invocation.getArgument(0));

    OrderResponse response = orderService.createOrder(request);

    assertNotNull(response);
    assertEquals(OrderStatus.CREATED, response.getStatus());
    assertEquals(new BigDecimal("100.00"), response.getTotalAmount());
    assertEquals(1, response.getItems().size());

    verify(orderRepository).save(any(Order.class));
  }

  @Test
  void createOrder_whenProductNotFound_shouldThrowProductNotFoundException() {
    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);

    OrderItemRequest itemRequest = new OrderItemRequest();
    itemRequest.setProductId(999L);
    itemRequest.setQuantity(1);
    request.setItems(List.of(itemRequest));

    when(userClient.getUser(1L))
      .thenReturn(new UserResponse(1L, "Alice", "alice@example.com"));

    when(productClient.getProduct(999L)).thenReturn(null);

    assertThrows(
      ProductNotFoundException.class,
      () -> orderService.createOrder(request)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void createOrder_whenProductInactive_shouldThrowProductNotFoundException() {
    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);

    OrderItemRequest itemRequest = new OrderItemRequest();
    itemRequest.setProductId(10L);
    itemRequest.setQuantity(1);
    request.setItems(List.of(itemRequest));

    ProductResponse productResponse = new ProductResponse(
      10L,
      "Widget",
      "Widget desc",
      new BigDecimal("50.00"),
      10,
      false
    );

    when(userClient.getUser(1L))
      .thenReturn(new UserResponse(1L, "Alice", "alice@example.com"));

    when(productClient.getProduct(10L)).thenReturn(productResponse);

    assertThrows(
      ProductNotFoundException.class,
      () -> orderService.createOrder(request)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void createOrder_whenInsufficientStock_shouldThrowInsufficientStockException() {
    CreateOrderRequest request = new CreateOrderRequest();
    request.setUserId(1L);

    OrderItemRequest itemRequest = new OrderItemRequest();
    itemRequest.setProductId(10L);
    itemRequest.setQuantity(15);
    request.setItems(List.of(itemRequest));

    ProductResponse productResponse = new ProductResponse(
      10L,
      "Widget",
      "Widget desc",
      new BigDecimal("50.00"),
      10,
      true
    );

    when(userClient.getUser(1L))
      .thenReturn(new UserResponse(1L, "Alice", "alice@example.com"));

    when(productClient.getProduct(10L)).thenReturn(productResponse);

    assertThrows(
      InsufficientStockException.class,
      () -> orderService.createOrder(request)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void getOrderById_whenFound_shouldReturnOrder() {
    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    OrderResponse response = orderService.getOrderById(1L);

    assertNotNull(response);
    assertEquals(OrderStatus.CREATED, response.getStatus());
    assertEquals(1L, response.getUserId());
  }

  @Test
  void getOrderById_whenNotFound_shouldThrowOrderNotFoundException() {
    when(orderRepository.findById(999L))
      .thenReturn(Optional.empty());

    assertThrows(
      OrderNotFoundException.class,
      () -> orderService.getOrderById(999L)
    );
  }

  @Test
  void getOrdersByUserId_shouldReturnList() {
    when(orderRepository.findByUserId(1L))
      .thenReturn(List.of(existingOrder));

    List<OrderResponse> responses =
      orderService.getOrdersByUserId(1L);

    assertEquals(1, responses.size());
    assertEquals(1L, responses.get(0).getUserId());
  }

  @Test
  void cancelOrder_fromCreated_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.CREATED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response = orderService.cancelOrder(1L);

    assertEquals(OrderStatus.CANCELLED, response.getStatus());
    verify(orderRepository).save(existingOrder);
  }

  @Test
  void cancelOrder_fromConfirmed_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.CONFIRMED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response = orderService.cancelOrder(1L);

    assertEquals(OrderStatus.CANCELLED, response.getStatus());
    verify(orderRepository).save(existingOrder);
  }

  @Test
  void cancelOrder_fromProcessing_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.PROCESSING);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.cancelOrder(1L)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void cancelOrder_fromShipped_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.SHIPPED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.cancelOrder(1L)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void cancelOrder_fromDelivered_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.DELIVERED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.cancelOrder(1L)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void cancelOrder_fromCancelled_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.CANCELLED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.cancelOrder(1L)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void cancelOrder_whenNotFound_shouldThrowOrderNotFoundException() {
    when(orderRepository.findById(999L))
      .thenReturn(Optional.empty());

    assertThrows(
      OrderNotFoundException.class,
      () -> orderService.cancelOrder(999L)
    );
  }

  @Test
  void updateOrderStatus_createdToConfirmed_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.CREATED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response =
      orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

    assertEquals(OrderStatus.CONFIRMED, response.getStatus());
    verify(orderRepository).save(existingOrder);
  }

  @Test
  void updateOrderStatus_confirmedToProcessing_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.CONFIRMED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response =
      orderService.updateOrderStatus(1L, OrderStatus.PROCESSING);

    assertEquals(OrderStatus.PROCESSING, response.getStatus());
  }

  @Test
  void updateOrderStatus_processingToShipped_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.PROCESSING);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response =
      orderService.updateOrderStatus(1L, OrderStatus.SHIPPED);

    assertEquals(OrderStatus.SHIPPED, response.getStatus());
  }

  @Test
  void updateOrderStatus_shippedToDelivered_shouldSucceed() {
    transitionOrderTo(existingOrder, OrderStatus.SHIPPED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    when(orderRepository.save(any(Order.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    OrderResponse response =
      orderService.updateOrderStatus(1L, OrderStatus.DELIVERED);

    assertEquals(OrderStatus.DELIVERED, response.getStatus());
  }

  @Test
  void updateOrderStatus_createdToShipped_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.CREATED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.updateOrderStatus(1L, OrderStatus.SHIPPED)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void updateOrderStatus_deliveredToCancelled_shouldThrowInvalidOrderStateException() {
    transitionOrderTo(existingOrder, OrderStatus.DELIVERED);

    when(orderRepository.findById(1L))
      .thenReturn(Optional.of(existingOrder));

    assertThrows(
      InvalidOrderStateException.class,
      () -> orderService.updateOrderStatus(1L, OrderStatus.CANCELLED)
    );

    verify(orderRepository, never()).save(any(Order.class));
  }

  @Test
  void updateOrderStatus_whenNotFound_shouldThrowOrderNotFoundException() {
    when(orderRepository.findById(999L))
      .thenReturn(Optional.empty());

    assertThrows(
      OrderNotFoundException.class,
      () -> orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED)
    );
  }
}