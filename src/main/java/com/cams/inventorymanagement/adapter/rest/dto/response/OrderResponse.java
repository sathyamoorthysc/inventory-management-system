package com.cams.inventorymanagement.adapter.rest.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id, LocalDateTime date, List<OrderItemResponse> items, String status, String totalPrice) {

  public record OrderItemResponse(UUID productId, int quantity, PriceResponse unitPrice) {}
}
