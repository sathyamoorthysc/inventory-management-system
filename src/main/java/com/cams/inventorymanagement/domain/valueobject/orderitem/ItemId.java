package com.cams.inventorymanagement.domain.valueobject.orderitem;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_ITEM_ID_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderItemException;
import java.util.UUID;

public record ItemId(UUID value) {
  public ItemId {
    if (value == null) {
      throw new InvalidOrderItemException(ORDER_ITEM_ID_NULL);
    }
  }
}
