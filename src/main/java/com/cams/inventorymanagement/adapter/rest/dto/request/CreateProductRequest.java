package com.cams.inventorymanagement.adapter.rest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateProductRequest(
    @NotBlank(message = "Product name is required") String name,
    @NotBlank(message = "Product SKU is required") String sku,
    @NotNull(message = "Product price is required") BigDecimal price,
    @NotNull(message = "Product stock is required") Integer stock) {}
