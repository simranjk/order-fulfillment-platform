package com.orderplatform.orderservice.service;

import com.orderplatform.orderservice.client.ProductClient;
import com.orderplatform.orderservice.client.UserClient;
import com.orderplatform.orderservice.dto.CreateOrderRequest;
import com.orderplatform.orderservice.dto.OrderItemRequest;
import com.orderplatform.orderservice.dto.OrderItemResponse;
import com.orderplatform.orderservice.dto.OrderResponse;
import com.orderplatform.orderservice.dto.ProductResponse;
import com.orderplatform.orderservice.entity.Order;
import com.orderplatform.orderservice.entity.OrderItem;
import com.orderplatform.orderservice.exception.InsufficientStockException;
import com.orderplatform.orderservice.exception.OrderNotFoundException;
import com.orderplatform.orderservice.exception.ProductNotFoundException;
import com.orderplatform.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

  private final OrderRepository orderRepository;
  private final ProductClient productClient;
  private final UserClient userClient;

  public OrderService(OrderRepository orderRepository,
                      ProductClient productClient,
                      UserClient userClient) {

    this.orderRepository = orderRepository;
    this.productClient = productClient;
    this.userClient = userClient;
  }

  @Transactional
  public OrderResponse createOrder(CreateOrderRequest request) {

    // Validate that the user exists
    userClient.getUser(request.getUserId());

    Order order = new Order();
    order.setUserId(request.getUserId());

    List<OrderItem> orderItems = new ArrayList<>();
    BigDecimal totalAmount = BigDecimal.ZERO;

    for (OrderItemRequest itemRequest : request.getItems()) {

      ProductResponse product =
        productClient.getProduct(itemRequest.getProductId());

      if (product == null) {
        throw new ProductNotFoundException(
          itemRequest.getProductId());
      }

      if (product.getActive() == null || !product.getActive()) {
        throw new ProductNotFoundException(
          itemRequest.getProductId());
      }

      if (product.getStockQuantity() == null ||
        product.getStockQuantity() < itemRequest.getQuantity()) {

        throw new InsufficientStockException(
          itemRequest.getProductId());
      }

      OrderItem orderItem = new OrderItem();

      orderItem.setProductId(product.getId());
      orderItem.setQuantity(itemRequest.getQuantity());

      // Store the price at the time the order is created.
      orderItem.setPrice(product.getPrice());

      orderItem.setOrder(order);

      orderItems.add(orderItem);

      BigDecimal itemTotal =
        product.getPrice()
          .multiply(
            BigDecimal.valueOf(
              itemRequest.getQuantity()
            )
          );

      totalAmount = totalAmount.add(itemTotal);
    }

    order.setItems(orderItems);
    order.setTotalAmount(totalAmount);

    Order savedOrder = orderRepository.save(order);

    return toResponse(savedOrder);
  }

  @Transactional(readOnly = true)
  public OrderResponse getOrderById(Long orderId) {

    Order order = orderRepository.findById(orderId)
      .orElseThrow(() ->
        new OrderNotFoundException(
          "Order not found: " + orderId));

    return toResponse(order);
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> getOrdersByUserId(Long userId) {

    List<Order> orders = orderRepository.findByUserId(userId);

    List<OrderResponse> responses = new ArrayList<>();

    for (Order order : orders) {
      responses.add(toResponse(order));
    }

    return responses;
  }

  private OrderResponse toResponse(Order order) {

    List<OrderItemResponse> itemResponses = new ArrayList<>();

    for (OrderItem item : order.getItems()) {

      itemResponses.add(
        new OrderItemResponse(
          item.getProductId(),
          item.getQuantity(),
          item.getPrice()
        )
      );
    }

    return new OrderResponse(
      order.getId(),
      order.getUserId(),
      order.getTotalAmount(),
      order.getStatus(),
      order.getCreatedAt(),
      itemResponses
    );
  }
}
      //Create Order
//     ↓
//For each product
//     ↓
//Call Product Service
//     ↓
//Check active
//     ↓
//Check stock
//     ↓
//Get current price
//     ↓
//Save price snapshot in OrderItem
//     ↓
//Calculate total
//     ↓
//Save Order