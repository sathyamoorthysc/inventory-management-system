package com.cams.inventorymanagement.adapter.rest.controller;

import com.cams.inventorymanagement.adapter.rest.dto.request.CreateOrderRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.UpdateOrderStatusRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.OrderResponse;
import com.cams.inventorymanagement.adapter.rest.service.OrderAdapterService;
import com.cams.inventorymanagement.application.dto.ProductOrderSummary;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
public class OrderController {
  private final OrderAdapterService orderAdapterService;

  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
    OrderResponse apiResponse = orderAdapterService.createOrder(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
  }

  @PatchMapping("/{orderId}/status")
  public ResponseEntity<OrderResponse> updateOrderStatus(
      @PathVariable UUID orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {
    OrderResponse apiResponse = orderAdapterService.updateOrderStatus(orderId, request);
    return ResponseEntity.ok().body(apiResponse);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
    OrderResponse apiResponse = orderAdapterService.getOrder(orderId);
    return ResponseEntity.ok().body(apiResponse);
  }

  @GetMapping("/summary/products")
  public ResponseEntity<List<ProductOrderSummary>> getOrderSummaryOfAllProducts() {
    List<ProductOrderSummary> apiResponse = orderAdapterService.getOrderSummaryOfAllProducts();
    return ResponseEntity.ok().body(apiResponse);
  }
}
