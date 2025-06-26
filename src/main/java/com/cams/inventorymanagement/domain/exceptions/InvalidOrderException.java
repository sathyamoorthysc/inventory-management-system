package com.cams.inventorymanagement.domain.exceptions;

/** Exception thrown when a Order is invalid or cannot be created/modified due to domain rules. */
public class InvalidOrderException extends DomainValidationException {
  public InvalidOrderException(String message) {
    super(message);
  }
}
