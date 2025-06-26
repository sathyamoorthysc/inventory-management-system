package com.cams.inventorymanagement.domain.valueobject.product;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_NAME_VALUE_NULL_OR_BLANK;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;

public record ProductName(String value) {
  private static final int MIN_LENGTH = 10;
  private static final int MAX_LENGTH = 150;

  public ProductName {
    if (value == null || value.isBlank()) {
      throw new InvalidProductException(PRODUCT_NAME_VALUE_NULL_OR_BLANK);
    }
    if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
      throw new InvalidProductException(
          "Product name must be between "
              + MIN_LENGTH
              + " and "
              + MAX_LENGTH
              + " characters long.");
    }
  }
}
