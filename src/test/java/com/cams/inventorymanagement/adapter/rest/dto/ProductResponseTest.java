package com.cams.inventorymanagement.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cams.inventorymanagement.adapter.rest.dto.response.PriceResponse;
import com.cams.inventorymanagement.adapter.rest.dto.response.ProductResponse;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductResponseTest {
  @Test
  void shouldCreateResponseWithAllFields() {
    UUID id = UUID.randomUUID();
    ProductResponse response =
        new ProductResponse(
            id, "Book", "SKU123", 5, new PriceResponse(new BigDecimal("10.00"), "INR"));
    assertEquals(id, response.id());
    assertEquals("Book", response.name());
    assertEquals("SKU123", response.sku());
    assertEquals(new BigDecimal("10.00"), response.unitPrice().amount());
    assertEquals("INR", response.unitPrice().currency());
    assertEquals(5, response.stock());
  }
}
