package com.cams.inventorymanagement.domain.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductTest {
  @Test
  void shouldCreateProductWhenAllFieldsAreValid() {
    Product product =
        new Product(
            new ProductId(UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479")),
            new ProductName("playstation"),
            new ProductSku("SKU123"),
            new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
            new ProductStock(10));

    assertNotNull(product);
    assertEquals("f47ac10b-58cc-4372-a567-0e02b2c3d479", product.id().value().toString());
    assertEquals("playstation", product.name().value());
    assertEquals("SKU123", product.sku().value());
    assertEquals("10.00", product.price().amount().toString());
    assertEquals("INR", product.price().currency().getCurrencyCode());
    assertEquals(10, product.stock().value());
  }

  @Test
  void shouldThrowExceptionWhenIdIsNull() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    null,
                    new ProductName("Keychain holder"),
                    new ProductSku("SKU123"),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductId cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNameIsNull() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    null,
                    new ProductSku("SKU123"),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductName cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenStockKeepingUnitIsNull() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("Crafting clay"),
                    null,
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductSku cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenPriceIsNull() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("playstation"),
                    new ProductSku("SKU123"),
                    null,
                    new ProductStock(10)));
    assertEquals("ProductPrice cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenStockIsNull() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("Keyboard and mouse"),
                    new ProductSku("SKU123"),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    null));
    assertEquals("ProductStock cannot be null", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNameIsBlank() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName(""),
                    new ProductSku("SKU123"),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductName value cannot be null or blank", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenStockKeepingUnitIsBlank() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("Crafting clay"),
                    new ProductSku(""),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductSku value cannot be null or blank", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenPriceIsNegative() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("playstation"),
                    new ProductSku("SKU123"),
                    new ProductPrice(new BigDecimal("-1.00"), Currency.getInstance("INR")),
                    new ProductStock(10)));
    assertEquals("ProductPrice value cannot be null or negative", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenStockIsNegative() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () ->
                new Product(
                    new ProductId(UUID.randomUUID()),
                    new ProductName("Keyboard and mouse"),
                    new ProductSku("SKU123"),
                    new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
                    new ProductStock(-1)));
    assertEquals("ProductStock value cannot be negative", ex.getMessage());
  }

  @Test
  void shouldSetAndGetVersionWhenProvided() {
    Product product =
        new Product(
            new ProductId(UUID.randomUUID()),
            new ProductName("Test Product"),
            new ProductSku("SKU123"),
            new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
            new ProductStock(10));
    product.version(7L);
    assertEquals(7L, product.version());
  }

  @Test
  void shouldBeEqualAndHashCodeWhenSameIdProvided() {
    ProductId id = new ProductId(UUID.randomUUID());
    Product product1 =
        new Product(
            id,
            new ProductName("Product112123"),
            new ProductSku("SKU112312312"),
            new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR")),
            new ProductStock(5));
    Product product2 =
        new Product(
            id,
            new ProductName("Product2123123"),
            new ProductSku("SKU2123123"),
            new ProductPrice(BigDecimal.ONE, Currency.getInstance("INR")),
            new ProductStock(2));
    assertEquals(product1, product2);
    assertEquals(product1.hashCode(), product2.hashCode());
  }
}
