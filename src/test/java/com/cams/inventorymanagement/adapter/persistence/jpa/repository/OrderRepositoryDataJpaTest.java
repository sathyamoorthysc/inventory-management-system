package com.cams.inventorymanagement.adapter.persistence.jpa.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.fixture.TestFixture;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class OrderRepositoryDataJpaTest {
  @Autowired private OrderEntityRepository orderEntityRepository;

  @Autowired private ProductEntityRepository productEntityRepository;

  @Test
  @DisplayName("Should save and find order with items")
  void testSaveAndFindOrderWithItems() {
    ProductEntity product = TestFixture.productEntityBuilder().sku("SKU-ORDER-ITEM").build();
    productEntityRepository.save(product);

    // Use TestFixture to create order item
    OrderItemEntity orderItem = TestFixture.orderItemEntityBuilder().product(product).build();

    // Use TestFixture to create order entity with the item
    UUID orderId = UUID.randomUUID();
    OrderEntity order =
        TestFixture.orderEntityBuilder().id(orderId).items(List.of(orderItem)).build();
    // Set the back-reference
    orderItem.order(order);

    orderEntityRepository.save(order);

    Optional<OrderEntity> orderFound = orderEntityRepository.findById(orderId);
    assertThat(orderFound).isPresent();
    assertThat(orderFound.get().items()).hasSize(1);
    assertThat(orderFound.get().items().get(0).product().id()).isEqualTo(product.id());
    assertThat(orderFound.get().items().get(0).quantity()).isEqualTo(orderItem.quantity());
  }
}
