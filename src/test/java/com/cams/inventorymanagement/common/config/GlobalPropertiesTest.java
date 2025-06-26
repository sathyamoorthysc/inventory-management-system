package com.cams.inventorymanagement.common.config;

import static com.cams.inventorymanagement.common.config.GlobalProperties.defaultCurrency;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GlobalPropertiesTest {
  @Test
  void shouldReturnCurrencyWhenSet() {
    assertEquals("INR", defaultCurrency().getCurrencyCode());
  }
}
