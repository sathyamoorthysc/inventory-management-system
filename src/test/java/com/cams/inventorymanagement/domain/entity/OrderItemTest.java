package com.cams.inventorymanagement.domain.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderItemException;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrderItemTest {
  @Test
  void shouldThrowExceptionWhenItemIdIsNullProvided() {
    InvalidOrderItemException ex =
        assertThrows(
            InvalidOrderItemException.class,
            () ->
                new OrderItem(
                    null,
                    new OrderId(UUID.randomUUID()),
                    new ProductId(UUID.randomUUID()),
                    new ItemQuantity(1),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"))));
    assertEquals("ItemId value cannot be null", ex.getMessage());
  }

  @Test
  void shouldNotThrowExceptionWhenOrderIdIsNullProvided() {
    assertDoesNotThrow(
        () ->
            new OrderItem(
                new ItemId(UUID.randomUUID()),
                null,
                new ProductId(UUID.randomUUID()),
                new ItemQuantity(1),
                new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"))));
  }

  @Test
  void shouldThrowExceptionWhenProductIdIsNullProvided() {
    InvalidOrderItemException ex =
        assertThrows(
            InvalidOrderItemException.class,
            () ->
                new OrderItem(
                    new ItemId(UUID.randomUUID()),
                    new OrderId(UUID.randomUUID()),
                    null,
                    new ItemQuantity(1),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"))));
    assertEquals("ProductId cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenQuantityIsNullProvided() {
    InvalidOrderItemException ex =
        assertThrows(
            InvalidOrderItemException.class,
            () ->
                new OrderItem(
                    new ItemId(UUID.randomUUID()),
                    new OrderId(UUID.randomUUID()),
                    new ProductId(UUID.randomUUID()),
                    null,
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"))));
    assertEquals("ItemQuantity value cannot be zero or negative", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenProductPriceIsNullProvided() {
    InvalidOrderItemException ex =
        assertThrows(
            InvalidOrderItemException.class,
            () ->
                new OrderItem(
                    new ItemId(UUID.randomUUID()),
                    new OrderId(UUID.randomUUID()),
                    new ProductId(UUID.randomUUID()),
                    new ItemQuantity(1),
                    null));
    assertEquals("ProductPrice cannot be null", ex.getMessage());
  }

  @Test
  void shouldCreateOrderItemSuccessfully() {
    OrderItem item =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            new ProductId(UUID.randomUUID()),
            new ItemQuantity(2),
            new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")));
    assertNotNull(item);
  }

  @Test
  void shouldReturnCorrectTotalWhenComputeItemTotalProvided() {
    OrderItem item =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            new ProductId(UUID.randomUUID()),
            new ItemQuantity(3),
            new ProductPrice(new BigDecimal("5.00"), Currency.getInstance("INR")));
    assertEquals(new BigDecimal("15.00"), item.computeItemTotal());
  }
}
