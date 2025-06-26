package com.cams.inventorymanagement.adapter.rest.dto.requestscope;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.fixture.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestScopedBeanTest {
  private RequestScopedBean bean;

  @BeforeEach
  void setUp() {
    bean = new RequestScopedBean();
  }

  @Test
  @DisplayName("Should add multiple products and retrieve them")
  void shouldAddAndRetrieveProductEntityFromMap() {
    ProductEntity product1 = TestFixture.productEntityBuilder().sku("SKU-1").build();
    ProductEntity product2 = TestFixture.productEntityBuilder().sku("SKU-2").build();
    bean.addProductEntity(product1);
    bean.addProductEntity(product2);
    assertEquals(2, bean.getProductEntities().size());
    assertTrue(bean.getProductEntities().containsKey(product1.id()));
    assertTrue(bean.getProductEntities().containsKey(product2.id()));
  }
}
