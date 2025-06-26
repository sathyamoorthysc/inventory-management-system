package com.cams.inventorymanagement.adapter.persistence.impl;

import static com.cams.inventorymanagement.fixture.TestFixture.productEntityBuilder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.OrderEntityRepository;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.requestscope.RequestScopedBean;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.fixture.TestFixture;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryImplTest {
  @Mock private OrderEntityRepository orderEntityRepository;
  @Mock private ProductEntityRepository productEntityRepository;
  @Mock private RequestScopedBean requestScopedBean;

  @InjectMocks private OrderRepositoryImpl orderRepositoryImpl;

  @Test
  void shouldSaveOrderSuccessfully() {
    UUID productId = UUID.randomUUID();
    ProductEntity productEntity = productEntityBuilder().id(productId).build();
    when(productEntityRepository.findById(productId)).thenReturn(Optional.of(productEntity));
    when(requestScopedBean.getProductEntities()).thenReturn(null);
    List<OrderItem> orderItems =
        List.of(TestFixture.orderItemBuilder().productId(new ProductId(productId)).build());
    Order order =
        TestFixture.orderBuilder().id(new OrderId(UUID.randomUUID())).items(orderItems).build();

    assertDoesNotThrow(() -> orderRepositoryImpl.save(order));

    verify(orderEntityRepository).save(any(OrderEntity.class));
  }

  @Test
  void shouldFetchProductEntityFromRequestScopedBeanWhenAvailable() {
    UUID productId = UUID.randomUUID();
    ProductEntity productEntity = productEntityBuilder().id(productId).build();
    Map<UUID, ProductEntity> productEntityMap = Map.of(productId, productEntity);
    when(requestScopedBean.getProductEntities()).thenReturn(productEntityMap);
    List<OrderItem> orderItems =
        List.of(TestFixture.orderItemBuilder().productId(new ProductId(productId)).build());
    Order order =
        TestFixture.orderBuilder().id(new OrderId(UUID.randomUUID())).items(orderItems).build();

    assertDoesNotThrow(() -> orderRepositoryImpl.save(order));

    verify(orderEntityRepository).save(any(OrderEntity.class));
    verify(productEntityRepository, never()).findById(any(UUID.class));
  }

  @Test
  void shouldThrowExceptionWhenProductNotFound() {
    UUID productId = UUID.randomUUID();
    when(productEntityRepository.findById(productId)).thenReturn(Optional.empty());
    List<OrderItem> orderItems =
        List.of(TestFixture.orderItemBuilder().productId(new ProductId(productId)).build());
    Order order =
        TestFixture.orderBuilder().id(new OrderId(UUID.randomUUID())).items(orderItems).build();

    RecordNotFoundException exception =
        assertThrows(RecordNotFoundException.class, () -> orderRepositoryImpl.save(order));

    verify(productEntityRepository).findById(productId);
    assertEquals("Product not found with ID: " + productId, exception.getMessage());
  }

  @Test
  void shouldReturnOrderWhenFound() {
    UUID orderId = UUID.randomUUID();
    ProductEntity productEntity = productEntityBuilder().id(orderId).build();
    OrderItemEntity orderItemEntity =
        TestFixture.orderItemEntityBuilder().product(productEntity).build();
    OrderEntity orderEntity =
        TestFixture.orderEntityBuilder().items(List.of(orderItemEntity)).build();
    orderItemEntity.order(orderEntity);
    when(orderEntityRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));

    Optional<Order> result = orderRepositoryImpl.findById(orderId);

    verify(orderEntityRepository).findById(orderId);
    assertTrue(result.isPresent());
  }

  @Test
  void shouldReturnEmptyWhenOrderNotFound() {
    UUID orderId = UUID.randomUUID();
    when(orderEntityRepository.findById(orderId)).thenReturn(Optional.empty());

    Optional<Order> result = orderRepositoryImpl.findById(orderId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnAllOrders() {
    ProductEntity productEntity = productEntityBuilder().id(UUID.randomUUID()).build();
    OrderItemEntity orderItemEntity =
        TestFixture.orderItemEntityBuilder().product(productEntity).build();
    OrderEntity orderEntity =
        TestFixture.orderEntityBuilder().items(List.of(orderItemEntity)).build();
    orderItemEntity.order(orderEntity);

    OrderItemEntity orderItemEntity2 =
        TestFixture.orderItemEntityBuilder().product(productEntity).build();
    OrderEntity orderEntity2 =
        TestFixture.orderEntityBuilder().items(List.of(orderItemEntity2)).build();
    orderItemEntity2.order(orderEntity2);

    when(orderEntityRepository.findAll()).thenReturn(List.of(orderEntity, orderEntity2));

    List<Order> result = orderRepositoryImpl.findAll();

    verify(orderEntityRepository).findAll();
    assertEquals(2, result.size());
  }
}
