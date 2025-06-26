package com.cams.inventorymanagement.domain.exceptions;

/** Base exceptions for all domain-related errors. */
public class DomainValidationException extends RuntimeException {
  public DomainValidationException(String message) {
    super(message);
  }
}
