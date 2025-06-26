package com.cams.inventorymanagement.domain.valueobject.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class ProductPriceTest {
  @Test
  void shouldCreatePriceWhenValidValuesProvided() {
    ProductPrice price = new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"));
    assertEquals("10.00", price.amount().toString());
    assertEquals("INR", price.currency().getCurrencyCode());
  }

  @Test
  void shouldThrowExceptionWhenNullPriceProvided() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () -> new ProductPrice(null, Currency.getInstance("INR")));
    assertEquals("ProductPrice value cannot be null or negative", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNegativePriceProvided() {
    InvalidProductException ex =
        assertThrows(
            InvalidProductException.class,
            () -> new ProductPrice(BigDecimal.valueOf(-1), Currency.getInstance("INR")));
    assertEquals("ProductPrice value cannot be null or negative", ex.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenNullCurrencyProvided() {
    InvalidProductException ex =
        assertThrows(InvalidProductException.class, () -> new ProductPrice(BigDecimal.TEN, null));
    assertEquals("ProductPrice currency cannot be null", ex.getMessage());
  }
}
