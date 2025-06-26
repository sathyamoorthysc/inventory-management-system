package com.cams.inventorymanagement.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderException;
import com.cams.inventorymanagement.domain.valueobject.order.OrderDate;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderTest {
  private final OrderId orderId =
      new OrderId(UUID.fromString("e647eb70-f9c1-49a4-8745-33f801f913ae"));
  private final ProductId productId =
      new ProductId(UUID.fromString("39b808ea-34ce-40a7-b6cc-14575362f49c"));
  private final OrderDate orderDate = new OrderDate(LocalDateTime.now());
  private final OrderItem item1 =
      new OrderItem(
          new ItemId(UUID.randomUUID()),
          orderId,
          productId,
          new ItemQuantity(2),
          new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")));
  private final OrderItem item2 =
      new OrderItem(
          new ItemId(UUID.randomUUID()),
          orderId,
          productId,
          new ItemQuantity(2),
          new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")));
  private final List<OrderItem> items = Arrays.asList(item1, item2);

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    InvalidOrderException ex =
        assertThrows(InvalidOrderException.class, () -> new Order(null, orderDate, items));
    assertEquals("OrderId cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenDateIsNullProvided() {
    InvalidOrderException ex =
        assertThrows(InvalidOrderException.class, () -> new Order(orderId, null, items));
    assertEquals("OrderDate cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenItemsIsNull() {
    InvalidOrderException exception =
        assertThrows(InvalidOrderException.class, () -> new Order(orderId, orderDate, null));
    assertEquals("OrderItems cannot be null or empty", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenItemsIsEmpty() {
    InvalidOrderException exception =
        assertThrows(
            InvalidOrderException.class,
            () -> new Order(orderId, orderDate, Collections.emptyList()));
    assertEquals("OrderItems cannot be null or empty", exception.getMessage());
  }

  @Test
  void shouldCreateOrderSuccessfully() {
    Order order = new Order(orderId, orderDate, items);
    assertEquals(orderId, order.orderId());
    assertEquals(orderDate, order.orderDate());
    assertEquals(items, order.orderItems());
    assertEquals(OrderStatus.PENDING, order.orderStatus());
  }

  @Test
  void shouldThrowExceptionWhenUpdateStatusWithNullProvided() {
    Order order = new Order(orderId, orderDate, items);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> order.updateStatus(null, orderId.value()));
    assertEquals("OrderStatus cannot be null or empty", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenUpdateStatusIfNotPendingProvided() {
    Order order = new Order(orderId, orderDate, items);
    order.updateStatus(OrderStatus.COMPLETED, order.orderId().value());
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> order.updateStatus(OrderStatus.CANCELLED, orderId.value()));
    assertEquals(
        "Cannot update status of a COMPLETED order with ID: e647eb70-f9c1-49a4-8745-33f801f913ae",
        exception.getMessage());
  }

  @Test
  void shouldUpdateStatusWhenPendingProvided() {
    Order order = new Order(orderId, orderDate, items);
    order.updateStatus(OrderStatus.COMPLETED, orderId.value());
    assertEquals(OrderStatus.COMPLETED, order.orderStatus());
  }

  @Test
  void shouldSetAndGetVersion() {
    Order order = new Order(orderId, orderDate, items);
    order.version(5L);
    assertEquals(5L, order.version());
  }

  @Test
  void shouldReturnTotalPriceWhenItemsProvided() {
    Order order = new Order(orderId, orderDate, items);
    BigDecimal expected = item1.computeItemTotal().add(item2.computeItemTotal());
    assertEquals(0, expected.compareTo(order.computeAndGetTotalPrice()));
  }

  @Test
  void shouldBeEqualAndHashCodeWhenSameIdProvided() {
    OrderId id = new OrderId(UUID.randomUUID());
    Order order1 = new Order(id, orderDate, items);
    Order order2 = new Order(id, orderDate, items);
    assertEquals(order1, order2);
    assertEquals(order1.hashCode(), order2.hashCode());
  }
}
