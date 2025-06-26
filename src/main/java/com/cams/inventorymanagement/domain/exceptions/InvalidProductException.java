package com.cams.inventorymanagement.domain.exceptions;

/** Exception thrown when a Product is invalid or cannot be created/modified due to domain rules. */
public class InvalidProductException extends DomainValidationException {
  public InvalidProductException(String message) {
    super(message);
  }
}
