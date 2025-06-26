package com.cams.inventorymanagement.adapter.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String error, String path, String status, String refId) {}
