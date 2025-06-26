package com.cams.inventorymanagement.adapter.rest.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(@NotNull List<OrderItemRequest> items) {
  public record OrderItemRequest(@NotNull UUID productId, @Min(1) int quantity) {}
}
