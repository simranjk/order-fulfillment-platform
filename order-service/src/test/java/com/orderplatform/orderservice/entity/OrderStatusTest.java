package com.orderplatform.orderservice.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

  @Test
  void createdTransitions() {
    assertTrue(OrderStatus.CREATED.canTransitionTo(OrderStatus.CONFIRMED));
    assertTrue(OrderStatus.CREATED.canTransitionTo(OrderStatus.CANCELLED));
    assertFalse(OrderStatus.CREATED.canTransitionTo(OrderStatus.PROCESSING));
    assertFalse(OrderStatus.CREATED.canTransitionTo(OrderStatus.SHIPPED));
    assertFalse(OrderStatus.CREATED.canTransitionTo(OrderStatus.DELIVERED));
    assertFalse(OrderStatus.CREATED.canTransitionTo(OrderStatus.CREATED));
    assertFalse(OrderStatus.CREATED.canTransitionTo(null));
    assertTrue(OrderStatus.CREATED.isCancellable());
  }

  @Test
  void confirmedTransitions() {
    assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.PROCESSING));
    assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.CANCELLED));
    assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.CREATED));
    assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.SHIPPED));
    assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.DELIVERED));
    assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.CONFIRMED));
    assertTrue(OrderStatus.CONFIRMED.isCancellable());
  }

  @Test
  void processingTransitions() {
    assertTrue(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.SHIPPED));
    assertFalse(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.CANCELLED));
    assertFalse(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.CONFIRMED));
    assertFalse(OrderStatus.PROCESSING.canTransitionTo(OrderStatus.DELIVERED));
    assertFalse(OrderStatus.PROCESSING.isCancellable());
  }

  @Test
  void shippedTransitions() {
    assertTrue(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.DELIVERED));
    assertFalse(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.CANCELLED));
    assertFalse(OrderStatus.SHIPPED.canTransitionTo(OrderStatus.PROCESSING));
    assertFalse(OrderStatus.SHIPPED.isCancellable());
  }

  @Test
  void deliveredTransitions() {
    for (OrderStatus target : OrderStatus.values()) {
      assertFalse(OrderStatus.DELIVERED.canTransitionTo(target));
    }
    assertFalse(OrderStatus.DELIVERED.isCancellable());
  }

  @Test
  void cancelledTransitions() {
    for (OrderStatus target : OrderStatus.values()) {
      assertFalse(OrderStatus.CANCELLED.canTransitionTo(target));
    }
    assertFalse(OrderStatus.CANCELLED.isCancellable());
  }
}
