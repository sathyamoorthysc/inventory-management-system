package com.cams.inventorymanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.application.exceptions.DuplicateSkuException;
import com.cams.inventorymanagement.application.port.ProductRepository;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
  @Mock private ProductRepository productRepository;
  @Mock private UuidGenerator uuidGenerator;
  @InjectMocks private ProductServiceImpl productService;

  @Test
  void shouldCreateProductSuccessfully() {
    UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    when(uuidGenerator.generate()).thenReturn(id);
    when(productRepository.existsBySku("SKU123")).thenReturn(false);
    doNothing().when(productRepository).save(any(Product.class));

    Product result = productService.createProduct("Adidas Shoe", "SKU123", BigDecimal.TEN, 10);

    assertNotNull(result);
    assertEquals(id.toString(), result.id().value().toString());
    verify(productRepository).save(any(Product.class));
  }

  @Test
  void shouldThrowExceptionWhenSkuIsNotUnique() {
    when(productRepository.existsBySku("SKU123")).thenReturn(true);

    DuplicateSkuException duplicateSkuException =
        assertThrows(
            DuplicateSkuException.class,
            () -> productService.createProduct("Book", "SKU123", BigDecimal.TEN, 10));

    assertEquals("Product SKU already exists: SKU123", duplicateSkuException.getMessage());
    verify(productRepository).existsBySku(anyString());
    verify(productRepository, never()).save(any(Product.class));
    verify(uuidGenerator, never()).generate();
  }

  @Test
  void shouldReturnProductsWithStockBelowThreshold() {
    Product product1 = getProductWithStock(2);
    Product product2 = getProductWithStock(5);
    Product product3 = getProductWithStock(6);
    when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));

    List<Product> result = productService.getProductsWithStockBelowThreshold(6);
    assertEquals(2, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoProductsWithStockBelowThreshold() {
    Product product1 = getProductWithStock(2);
    Product product2 = getProductWithStock(5);
    Product product3 = getProductWithStock(6);
    when(productRepository.findAll()).thenReturn(List.of(product1, product2, product3));

    List<Product> result = productService.getProductsWithStockBelowThreshold(1);
    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenNoProductsExist() {
    when(productRepository.findAll()).thenReturn(List.of());
    List<Product> result = productService.getProductsWithStockBelowThreshold(1);
    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnEmptyListWhenProductsIsNull() {
    when(productRepository.findAll()).thenReturn(null);
    List<Product> result = productService.getProductsWithStockBelowThreshold(1);
    assertEquals(0, result.size());
  }

  @Test
  void shouldDecrementStockSuccessfully() {
    Product product = getProductWithStock(10);
    ProductId productId = product.id();
    when(productRepository.findById(productId.value())).thenReturn(java.util.Optional.of(product));
    doNothing().when(productRepository).save(any(Product.class));

    Product result = productService.decrementStock(productId, 3);

    assertEquals(7, result.stock().value());
    verify(productRepository).save(product);
  }

  @Test
  void shouldIncrementStockSuccessfully() {
    UUID id = UUID.randomUUID();
    Product product = getProductWithStock(5);
    ProductId productId = product.id();
    when(productRepository.findById(productId.value())).thenReturn(java.util.Optional.of(product));
    doNothing().when(productRepository).save(any(Product.class));

    Product result = productService.incrementStock(productId, 4);

    assertEquals(9, result.stock().value());
    verify(productRepository).save(product);
  }

  @Test
  void shouldThrowExceptionWhenDecrementingNonExistingProduct() {
    ProductId productId = new ProductId(UUID.randomUUID());
    when(productRepository.findById(productId.value())).thenReturn(java.util.Optional.empty());

    assertThrows(
        com.cams.inventorymanagement.application.exceptions.RecordNotFoundException.class,
        () -> productService.decrementStock(productId, 2));
  }

  @Test
  void shouldThrowExceptionWhenIncrementingNonExistingProduct() {
    ProductId productId = new ProductId(UUID.randomUUID());
    when(productRepository.findById(productId.value())).thenReturn(java.util.Optional.empty());

    assertThrows(
        com.cams.inventorymanagement.application.exceptions.RecordNotFoundException.class,
        () -> productService.incrementStock(productId, 2));
  }

  @Test
  void shouldThrowExceptionWhenStockThresholdIsZeroOrNegative() {
    assertThrows(
        IllegalArgumentException.class, () -> productService.getProductsWithStockBelowThreshold(0));
    assertThrows(
        IllegalArgumentException.class,
        () -> productService.getProductsWithStockBelowThreshold(-5));
  }

  private static Product getProductWithStock(int stock) {
    return new Product(
        new ProductId(UUID.randomUUID()),
        new ProductName("Stylish Tshirt"),
        new ProductSku("SKU123"),
        new ProductPrice(BigDecimal.valueOf(10), Currency.getInstance("INR")),
        new ProductStock(stock));
  }
}
