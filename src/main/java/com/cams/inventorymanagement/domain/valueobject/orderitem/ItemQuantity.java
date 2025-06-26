package com.cams.inventorymanagement.domain.valueobject.orderitem;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_QUANTITY_VALUE_ZERO_OR_NEGATIVE;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderItemException;

public record ItemQuantity(int value) {
  private static final int MIN_QUANTITY = 1;

  public ItemQuantity {
    if (value < MIN_QUANTITY) {
      throw new InvalidOrderItemException(ORDER_QUANTITY_VALUE_ZERO_OR_NEGATIVE);
    }
  }
}
