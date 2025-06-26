package com.cams.inventorymanagement.domain.exceptions;

/** Exception thrown when there is insufficient stock for an order item. */
public class InsufficientStockException extends DomainValidationException {
  public InsufficientStockException(String message) {
    super(message);
  }
}
