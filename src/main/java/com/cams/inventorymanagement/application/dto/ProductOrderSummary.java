package com.cams.inventorymanagement.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductOrderSummary(UUID productId, BigDecimal totalOrderValue) {}
