package com.cams.inventorymanagement.adapter.rest.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cams.inventorymanagement.adapter.rest.dto.request.CreateProductRequest;
import com.cams.inventorymanagement.common.BeanValidationUtil;
import jakarta.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CreateProductRequestTest {
  @Test
  void shouldReturnCreateProductRequestProvidedValidArguments() {
    CreateProductRequest req =
        new CreateProductRequest("Book", "SKU123", new BigDecimal("10.00"), 5);
    Set<ConstraintViolation<CreateProductRequest>> violations =
        BeanValidationUtil.validateAndGetViolations(req);
    assertTrue(violations.isEmpty());
  }

  @Test
  void shouldFailValidationWhenAmountIsNull() {
    CreateProductRequest req = new CreateProductRequest("Book", "SKU123", null, 5);
    Set<ConstraintViolation<CreateProductRequest>> violations =
        BeanValidationUtil.validateAndGetViolations(req);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("Product price is required")));
  }

  @Test
  void shouldFailValidationWhenStockIsNull() {
    CreateProductRequest req =
        new CreateProductRequest("Book", "SKU123", new BigDecimal("10.00"), null);
    Set<ConstraintViolation<CreateProductRequest>> violations =
        BeanValidationUtil.validateAndGetViolations(req);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("Product stock is required")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  void shouldFailValidationWhenNameIsBlankOrWhitespace(String name) {
    CreateProductRequest req = new CreateProductRequest(name, "SKU123", new BigDecimal("10.00"), 5);
    Set<ConstraintViolation<CreateProductRequest>> violations =
        BeanValidationUtil.validateAndGetViolations(req);
    assertFalse(violations.isEmpty());
    assertTrue(
        violations.stream().anyMatch(v -> v.getMessage().equals("Product name is required")));
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "   "})
  void shouldFailValidationWhenSkuIsBlankOrWhitespace(String sku) {
    CreateProductRequest req = new CreateProductRequest("Book", sku, new BigDecimal("10.00"), 5);
    Set<ConstraintViolation<CreateProductRequest>> violations =
        BeanValidationUtil.validateAndGetViolations(req);
    assertFalse(violations.isEmpty());
    assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Product SKU is required")));
  }
}
