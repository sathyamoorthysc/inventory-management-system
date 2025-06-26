package com.cams.inventorymanagement.domain.valueobject.order;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_DATE_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderException;
import java.time.LocalDateTime;

public record OrderDate(LocalDateTime value) {
  public OrderDate {
    if (value == null) {
      throw new InvalidOrderException(ORDER_DATE_NULL);
    }
  }
}
