package com.cams.inventorymanagement.domain.valueobject.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductIdTest {
  @Test
  void shouldCreateIdWhenValidUuidProvided() {
    UUID uuid = UUID.randomUUID();
    ProductId productId = new ProductId(uuid);
    assertEquals(uuid, productId.value());
  }

  @Test
  void shouldThrowExceptionWhenNullUuidProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductId(null));
    assertEquals("ProductId value cannot be null", ex.getMessage());
  }
}
