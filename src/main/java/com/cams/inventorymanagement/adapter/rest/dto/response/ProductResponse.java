package com.cams.inventorymanagement.adapter.rest.dto.response;

import java.util.UUID;

public record ProductResponse(
    UUID id, String name, String sku, Integer stock, PriceResponse unitPrice) {}
