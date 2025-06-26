package com.cams.inventorymanagement.adapter.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddStockRequest(@NotNull @Min(1) Integer stockToAdd) {}
