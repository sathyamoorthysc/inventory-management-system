package com.cams.inventorymanagement.common.config;

import java.util.Currency;

/**
 * Global properties for the inventory management application. This class can be used to bind
 * properties from the application configuration file.
 */
public class GlobalProperties {
  private GlobalProperties() {}

  public static Currency defaultCurrency() {
    return Currency.getInstance("INR");
  }
}
