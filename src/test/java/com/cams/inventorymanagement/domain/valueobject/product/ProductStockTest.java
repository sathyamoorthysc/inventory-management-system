package com.cams.inventorymanagement.domain.valueobject.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import org.junit.jupiter.api.Test;

class ProductStockTest {
  @Test
  void shouldCreateStockWhenNonNegativeValueProvided() {
    ProductStock stock = new ProductStock(10);
    assertEquals(10, stock.value());
  }

  @Test
  void shouldThrowExceptionWhenNegativeStockProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductStock(-1));
    assertEquals("ProductStock value cannot be negative", ex.getMessage());
  }
}
