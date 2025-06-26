package com.cams.inventorymanagement.domain.valueobject.product;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_ID_VALUE_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.util.UUID;

public record ProductId(UUID value) {
  public ProductId {
    if (value == null) {
      throw new InvalidProductException(PRODUCT_ID_VALUE_NULL);
    }
  }
}
