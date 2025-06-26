package com.cams.inventorymanagement.adapter.rest.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.adapter.rest.dto.request.AddStockRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateProductRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.PriceResponse;
import com.cams.inventorymanagement.adapter.rest.dto.response.ProductResponse;
import com.cams.inventorymanagement.adapter.rest.service.ProductAdapterService;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
  @Mock private ProductAdapterService productAdapterService;
  @InjectMocks private ProductController productController;

  @Test
  void shouldReturnResponseWhenProductCreated() {
    CreateProductRequest request =
        new CreateProductRequest("Book", "SKU123", new BigDecimal("10.00"), 5);
    ProductResponse expectedResponse =
        new ProductResponse(
            UUID.randomUUID(),
            "Book",
            "SKU123",
            5,
            new PriceResponse(new BigDecimal("10.00"), "INR"));
    when(productAdapterService.createProduct(any(CreateProductRequest.class)))
        .thenReturn(expectedResponse);

    ResponseEntity<ProductResponse> response = productController.createProduct(request);

    assertEquals(201, response.getStatusCode().value());
    assertThat(response.getBody()).isEqualTo(expectedResponse);
    verify(productAdapterService).createProduct(request);
  }

  @Test
  void shouldReturnProductsWithStockLessThanThreshold() {
    int threshold = 3;
    ProductResponse product1 =
        new ProductResponse(
            UUID.randomUUID(), "Pen", "SKU1", 2, new PriceResponse(new BigDecimal("2.00"), "INR"));
    ProductResponse product2 =
        new ProductResponse(
            UUID.randomUUID(),
            "Pencil",
            "SKU2",
            1,
            new PriceResponse(new BigDecimal("1.00"), "INR"));
    List<ProductResponse> expectedList = List.of(product1, product2);
    when(productAdapterService.getProductsWithStockBelowThreshold(threshold))
        .thenReturn(expectedList);

    ResponseEntity<List<ProductResponse>> response =
        productController.getProductsWithStockBelowThreshold(threshold);

    assertEquals(200, response.getStatusCode().value());
    assertThat(response.getBody()).containsExactlyElementsOf(expectedList);
    verify(productAdapterService).getProductsWithStockBelowThreshold(threshold);
  }

  @Test
  void shouldReturnEmptyListWhenNoProductsHasStockLessThanThreshold() {
    int threshold = 1;
    when(productAdapterService.getProductsWithStockBelowThreshold(threshold)).thenReturn(List.of());

    ResponseEntity<List<ProductResponse>> response =
        productController.getProductsWithStockBelowThreshold(threshold);

    assertEquals(200, response.getStatusCode().value());
    assertThat(response.getBody()).isEmpty();
    verify(productAdapterService).getProductsWithStockBelowThreshold(threshold);
  }

  @Test
  void shouldIncreaseProductStockWhenAddStockCalled() {
    UUID productId = UUID.randomUUID();
    AddStockRequest addStockRequest = new AddStockRequest(5);
    ProductResponse updatedProduct =
        new ProductResponse(
            productId, "Book", "SKU123", 10, new PriceResponse(new BigDecimal("10.00"), "INR"));
    when(productAdapterService.addProductStock(productId, addStockRequest))
        .thenReturn(updatedProduct);

    ResponseEntity<ProductResponse> response =
        productController.addProductStock(productId, addStockRequest);

    assertEquals(200, response.getStatusCode().value());
    assertThat(response.getBody()).isEqualTo(updatedProduct);
    verify(productAdapterService).addProductStock(productId, addStockRequest);
  }
}
