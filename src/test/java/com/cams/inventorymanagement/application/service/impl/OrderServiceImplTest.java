package com.cams.inventorymanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.application.dto.ItemOrderDetail;
import com.cams.inventorymanagement.application.dto.ProductOrderSummary;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.application.port.OrderRepository;
import com.cams.inventorymanagement.application.service.ProductService;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.order.OrderDate;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.fixture.TestFixture;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
  @Mock UuidGenerator uuidGenerator;
  @Mock OrderRepository orderRepository;
  @Mock ProductService productService;
  @InjectMocks OrderServiceImpl orderService;

  @Test
  void shouldCreateOrderWithValidItemsAndDecrementStock() {
    UUID orderId = UUID.randomUUID();
    Product product = TestFixture.productBuilder().build();
    when(uuidGenerator.generate()).thenReturn(orderId, UUID.randomUUID());
    when(productService.decrementStock(any(), anyInt())).thenReturn(product);
    ItemOrderDetail detail = new ItemOrderDetail(product.id().value(), 2);
    doNothing().when(orderRepository).save(any(Order.class));

    Order order = orderService.createOrder(List.of(detail));

    assertNotNull(order);
    assertEquals(OrderStatus.PENDING, order.orderStatus());
    verify(productService, times(1)).decrementStock(product.id(), 2);
    verify(orderRepository, times(1)).save(any());
  }

  @Test
  void shouldThrowWhenCreateOrderWithNullOrEmptyInputItems() {
    assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(null));
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(List.of()));
    assertEquals("Item details cannot be null or empty.", exception.getMessage());
  }

  @Test
  void shouldUpdateOrderStatusWhenValidAndDifferent() {
    UUID orderId = UUID.randomUUID();
    OrderId oid = new OrderId(orderId);
    Order order = mock(Order.class);
    when(order.orderStatus()).thenReturn(OrderStatus.PENDING);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    doNothing().when(orderRepository).save(order);
    doNothing().when(order).updateStatus(OrderStatus.COMPLETED, orderId);

    Order result = orderService.updateOrderStatus(oid, OrderStatus.COMPLETED);

    assertNotNull(result);
    verify(order).updateStatus(OrderStatus.COMPLETED, orderId);
    verify(orderRepository).save(order);
  }

  @Test
  void shouldNotUpdateOrderStatusIfSame() {
    UUID orderId = UUID.randomUUID();
    OrderId oid = new OrderId(orderId);
    Order order = mock(Order.class);
    when(order.orderStatus()).thenReturn(OrderStatus.PENDING);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    Order result = orderService.updateOrderStatus(oid, OrderStatus.PENDING);

    assertNotNull(result);
    verify(order, never()).updateStatus(OrderStatus.COMPLETED, orderId);
    verify(orderRepository, never()).save(order);
  }

  @Test
  void shouldThrowWhenUpdateOrderStatusWithNullStatus() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> orderService.updateOrderStatus(new OrderId(UUID.randomUUID()), null));
    assertEquals("Order status cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowWhenUpdateOrderStatusWithNotFoundOrder() {
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    RecordNotFoundException exception =
        assertThrows(
            RecordNotFoundException.class,
            () -> orderService.updateOrderStatus(new OrderId(orderId), OrderStatus.COMPLETED));
    assertEquals("Order does not exists with id: " + orderId.toString(), exception.getMessage());
  }

  @Test
  void shouldIncrementStockWhenOrderCancelled() {
    UUID orderId = UUID.randomUUID();
    OrderId oid = new OrderId(orderId);
    Product product = TestFixture.productBuilder().build();
    OrderItem item =
        new OrderItem(
            new ItemId(UUID.randomUUID()), oid, product.id(), new ItemQuantity(2), product.price());
    Order order = mock(Order.class);
    when(order.orderStatus()).thenReturn(OrderStatus.PENDING);
    when(order.orderItems()).thenReturn(List.of(item));
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
    doNothing().when(orderRepository).save(order);
    doNothing().when(order).updateStatus(OrderStatus.CANCELLED, orderId);

    orderService.updateOrderStatus(oid, OrderStatus.CANCELLED);
    verify(productService, times(1)).incrementStock(product.id(), 2);
  }

  @Test
  void shouldReturnOrderByIdIfExists() {
    UUID orderId = UUID.randomUUID();
    Order order = mock(Order.class);
    when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

    Order result = orderService.getOrderById(new OrderId(orderId));
    assertEquals(order, result);
  }

  @Test
  void shouldThrowWhenGetOrderByIdWithNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(null));
    assertEquals("Order ID cannot be null.", exception.getMessage());
  }

  @Test
  void shouldThrowWhenGetOrderByIdNotFound() {
    UUID orderId = UUID.randomUUID();
    when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
    RecordNotFoundException exception =
        assertThrows(
            RecordNotFoundException.class, () -> orderService.getOrderById(new OrderId(orderId)));
    assertEquals("Order does not exists with id: " + orderId, exception.getMessage());
  }

  @Test
  void shouldReturnOrderValueSummaryOfAllProducts() {
    Product product1 = TestFixture.productBuilder().build();
    Product product2 = TestFixture.productBuilder().build();
    OrderItem item1 =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            product1.id(),
            new ItemQuantity(2),
            product1.price());
    OrderItem item2 =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            product2.id(),
            new ItemQuantity(1),
            product2.price());
    OrderItem item3 =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            product1.id(),
            new ItemQuantity(3),
            product1.price());
    OrderItem item4 =
        new OrderItem(
            new ItemId(UUID.randomUUID()),
            new OrderId(UUID.randomUUID()),
            product2.id(),
            new ItemQuantity(2),
            product2.price());
    Order order1 =
        new Order(
            new OrderId(UUID.randomUUID()),
            new OrderDate(LocalDateTime.now()),
            List.of(item1, item2));
    order1.updateStatus(OrderStatus.COMPLETED, order1.orderId().value());
    Order order2 =
        new Order(
            new OrderId(UUID.randomUUID()),
            new OrderDate(LocalDateTime.now()),
            List.of(item3, item4));
    order2.updateStatus(OrderStatus.COMPLETED, order2.orderId().value());
    when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

    List<ProductOrderSummary> summary = orderService.getOrderValueSummaryOfAllProducts();

    BigDecimal expected1 = product1.price().amount().multiply(BigDecimal.valueOf(5));
    BigDecimal expected2 = product2.price().amount().multiply(BigDecimal.valueOf(3));
    assertEquals(expected1, summary.get(0).totalOrderValue());
    assertEquals(expected2, summary.get(1).totalOrderValue());
  }

  @Test
  void shouldReturnEmptySummaryWhenNoOrders() {
    when(orderRepository.findAll()).thenReturn(List.of());
    List<ProductOrderSummary> summary = orderService.getOrderValueSummaryOfAllProducts();
    assertTrue(summary.isEmpty());
  }
}
