package com.cams.inventorymanagement.adapter.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

public record PriceResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING) BigDecimal amount, String currency) {}
