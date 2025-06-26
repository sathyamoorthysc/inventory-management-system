package com.cams.inventorymanagement.domain.valueobject.order;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_ITEM_ID_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.util.UUID;

public record OrderId(UUID value) {
  public OrderId {
    if (value == null) {
      throw new InvalidProductException(ORDER_ITEM_ID_NULL);
    }
  }
}
