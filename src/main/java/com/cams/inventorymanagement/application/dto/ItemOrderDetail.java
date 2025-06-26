package com.cams.inventorymanagement.application.dto;

import java.util.UUID;

public record ItemOrderDetail(UUID productId, int quantity) {}
