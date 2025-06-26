package com.cams.inventorymanagement.domain.entity;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_DATE_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_ID_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_ITEMS_NULL_OR_EMPTY;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_STATUS_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderException;
import com.cams.inventorymanagement.domain.valueobject.order.OrderDate;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// Using class because order data are mutated frequently, such as order status.
// Not using lombok to keep domain logic independent of external libraries. But we can use it based
// on project standards that reduce boilerplate codes.

public final class Order {
  private final OrderId id;
  private final OrderDate date;
  private final List<OrderItem> orderItems;
  private OrderStatus status;
  private Long version;

  public Order(OrderId id, OrderDate date, List<OrderItem> orderItems) {
    if (id == null) {
      throw new InvalidOrderException(ORDER_ID_NULL);
    }
    if (date == null) {
      throw new InvalidOrderException(ORDER_DATE_NULL);
    }
    if (orderItems == null || orderItems.isEmpty()) {
      throw new InvalidOrderException(ORDER_ITEMS_NULL_OR_EMPTY);
    }
    this.id = id;
    this.date = date;
    this.orderItems = orderItems;
    this.status = OrderStatus.PENDING;
  }

  public OrderId orderId() {
    return id;
  }

  public OrderDate orderDate() {
    return date;
  }

  public List<OrderItem> orderItems() {
    return orderItems;
  }

  public OrderStatus orderStatus() {
    return status;
  }

  public boolean isCancelled() {
    return this.status.name().equals(OrderStatus.CANCELLED.name());
  }

  public void updateStatus(OrderStatus newStatus, UUID orderId) {
    if (newStatus == null) {
      throw new IllegalArgumentException(ORDER_STATUS_NULL);
    }
    if (this.status != OrderStatus.PENDING) {
      throw new IllegalStateException(
          "Cannot update status of a " + this.status.name() + " order with ID: " + orderId);
    }
    this.status = newStatus;
  }

  public Long version() {
    return version;
  }

  public void version(Long newVersion) {
    this.version = newVersion;
  }

  public BigDecimal computeAndGetTotalPrice() {
    return orderItems.stream()
        .map(OrderItem::computeItemTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    var that = (Order) obj;
    return Objects.equals(this.id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
