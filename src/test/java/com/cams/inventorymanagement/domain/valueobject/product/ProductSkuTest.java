package com.cams.inventorymanagement.domain.valueobject.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import org.junit.jupiter.api.Test;

class ProductSkuTest {
  @Test
  void shouldCreateStockKeepingUnitWhenValidValueProvided() {
    ProductSku sku = new ProductSku("SKU123");
    assertEquals("SKU123", sku.value());
  }

  @Test
  void shouldThrowExceptionWhenNullSkuProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductSku(null));
    assertEquals("ProductSku value cannot be null or blank", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenBlankSkuProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductSku("   "));
    assertEquals("ProductSku value cannot be null or blank", ex.getMessage());
  }
}
