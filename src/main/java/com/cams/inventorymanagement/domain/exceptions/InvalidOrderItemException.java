package com.cams.inventorymanagement.domain.exceptions;

/**
 * Exception thrown when a OrderItem is invalid or cannot be created/modified due to domain rules.
 */
public class InvalidOrderItemException extends DomainValidationException {
  public InvalidOrderItemException(String message) {
    super(message);
  }
}
