package com.cams.inventorymanagement.adapter.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.fixture.TestFixture;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class ProductRepositoryDataJpaTest {
  @Autowired private ProductEntityRepository productEntityRepository;

  @Test
  @DisplayName("Should save and find product by id")
  void testSaveAndFindById() {
    ProductEntity entity = TestFixture.productEntityBuilder().sku("SKU-123").build();
    productEntityRepository.save(entity);

    Optional<ProductEntity> productFound = productEntityRepository.findById(entity.id());
    assertThat(productFound).isPresent();
    assertThat(productFound.get().sku()).isEqualTo("SKU-123");
  }

  @Test
  @DisplayName("Should check existence by SKU")
  void testExistsBySku() {
    ProductEntity entity = TestFixture.productEntityBuilder().sku("SKU-EXISTS").build();
    productEntityRepository.save(entity);

    boolean exists = productEntityRepository.existsBySku("SKU-EXISTS");
    assertThat(exists).isTrue();
  }
}
