package com.cams.inventorymanagement.adapter.rest.dto.request;

import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
