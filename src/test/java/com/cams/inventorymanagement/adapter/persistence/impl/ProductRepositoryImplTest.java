package com.cams.inventorymanagement.adapter.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.requestscope.RequestScopedBean;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.fixture.TestFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {
  @Mock private ProductEntityRepository productEntityRepository;
  @Mock private RequestScopedBean requestScopedBean;
  @InjectMocks private ProductRepositoryImpl productRepositoryImpl;

  @Test
  void shouldReturnProductWhenFound() {
    UUID id = UUID.randomUUID();
    ProductEntity entity = TestFixture.productEntityBuilder().id(id).build();
    when(productEntityRepository.findById(id)).thenReturn(Optional.of(entity));

    Optional<Product> result = productRepositoryImpl.findById(id);

    assertTrue(result.isPresent());
    assertEquals(id, result.get().id().value());
  }

  @Test
  void shouldReturnEmptyWhenProductNotFound() {
    UUID id = UUID.randomUUID();
    when(productEntityRepository.findById(id)).thenReturn(Optional.empty());
    Optional<Product> result = productRepositoryImpl.findById(id);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldSaveProductSuccessfully() {
    Product product = TestFixture.productBuilder().build();

    productRepositoryImpl.save(product);

    verify(productEntityRepository).save(any(ProductEntity.class));
  }

  @Test
  void shouldAddProductToRequestScopedBeanWhenSaving() {
    RequestScopedBean cacheBean = new RequestScopedBean();
    productRepositoryImpl = new ProductRepositoryImpl(productEntityRepository, cacheBean);

    UUID productId = UUID.randomUUID();
    ProductEntity entity = TestFixture.productEntityBuilder().id(productId).build();
    when(productEntityRepository.findById(productId)).thenReturn(Optional.of(entity));

    productRepositoryImpl.findById(productId);

    assertNotNull(cacheBean.getProductEntities());
    assertNotNull(cacheBean.getProductEntities().get(productId));
    verify(productEntityRepository).findById(any(UUID.class));
  }

  @Test
  void shouldReturnTrueWhenProductExistsBySku() {
    String sku = "SKU-001";
    when(productEntityRepository.existsBySku(sku)).thenReturn(true);

    boolean exists = productRepositoryImpl.existsBySku(sku);

    assertTrue(exists);
    verify(productEntityRepository).existsBySku(sku);
  }

  @Test
  void shouldReturnFalseWhenProductExistsBySku() {
    String sku = "SKU-001";
    when(productEntityRepository.existsBySku(sku)).thenReturn(false);

    boolean exists = productRepositoryImpl.existsBySku(sku);

    assertFalse(exists);
    verify(productEntityRepository).existsBySku(sku);
  }

  @Test
  void shouldReturnAllProducts() {
    ProductEntity entity1 = TestFixture.productEntityBuilder().build();
    ProductEntity entity2 = TestFixture.productEntityBuilder().build();
    when(productEntityRepository.findAll()).thenReturn(List.of(entity1, entity2));

    List<Product> products = productRepositoryImpl.findAll();

    assertNotNull(products);
    assertEquals(2, products.size());
    verify(productEntityRepository).findAll();
  }
}
