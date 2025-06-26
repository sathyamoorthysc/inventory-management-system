package com.cams.inventorymanagement.domain.valueobject.product;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_PRICE_CURRENCY_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_PRICE_VALUE_NULL_OR_NEGATIVE;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record ProductPrice(BigDecimal amount, Currency currency) {
  private static final int AMOUNT_SCALE = 2;

  public ProductPrice {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new InvalidProductException(PRODUCT_PRICE_VALUE_NULL_OR_NEGATIVE);
    }
    if (currency == null) {
      throw new InvalidProductException(PRODUCT_PRICE_CURRENCY_NULL);
    }
    amount = amount.setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
  }
}
