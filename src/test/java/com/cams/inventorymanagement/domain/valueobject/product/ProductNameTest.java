package com.cams.inventorymanagement.domain.valueobject.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import org.junit.jupiter.api.Test;

class ProductNameTest {
  @Test
  void shouldCreateNameProvided() {
    ProductName name = new ProductName("ValidProductName");
    assertEquals("ValidProductName", name.value());
  }

  @Test
  void shouldThrowExceptionWhenNullNameProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductName(null));
    assertEquals("ProductName value cannot be null or blank", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenBlankNameProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductName("   "));
    assertEquals("ProductName value cannot be null or blank", ex.getMessage());
  }
}
