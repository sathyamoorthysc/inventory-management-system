package com.cams.inventorymanagement.adapter.rest.controller;

import com.cams.inventorymanagement.adapter.rest.dto.request.AddStockRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateProductRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.ProductResponse;
import com.cams.inventorymanagement.adapter.rest.service.ProductAdapterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/products")
public class ProductController {
  private final ProductAdapterService productAdapterService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody CreateProductRequest request) {
    ProductResponse response = productAdapterService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/stock")
  public ResponseEntity<List<ProductResponse>> getProductsWithStockBelowThreshold(
      @RequestParam("stockLt") @NotNull @Min(1) Integer stockThreshold) {
    List<ProductResponse> responses =
        productAdapterService.getProductsWithStockBelowThreshold(stockThreshold);
    return ResponseEntity.ok(responses);
  }

  @PatchMapping("{productId}/stock")
  public ResponseEntity<ProductResponse> addProductStock(
      @PathVariable @NotNull UUID productId, @Valid @RequestBody AddStockRequest addStockRequest) {
    ProductResponse response = productAdapterService.addProductStock(productId, addStockRequest);
    return ResponseEntity.ok(response);
  }

  // We can also add support for decrement stock in ProductController
}
