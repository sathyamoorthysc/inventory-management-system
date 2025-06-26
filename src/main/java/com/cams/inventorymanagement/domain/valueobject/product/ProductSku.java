package com.cams.inventorymanagement.domain.valueobject.product;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_SKU_VALUE_NULL_OR_BLANK;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;

public record ProductSku(String value) {
  private static final int MIN_LENGTH = 5;
  private static final int MAX_LENGTH = 30;

  public ProductSku {
    if (value == null || value.isBlank()) {
      throw new InvalidProductException(PRODUCT_SKU_VALUE_NULL_OR_BLANK);
    }
    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new InvalidProductException(
          "Product SKU must be between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters long.");
    }
  }
}
