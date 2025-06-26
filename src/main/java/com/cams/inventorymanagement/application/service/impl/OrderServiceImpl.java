package com.cams.inventorymanagement.application.service.impl;

import static com.cams.inventorymanagement.common.config.ClockConfig.localDateTime;

import com.cams.inventorymanagement.application.dto.ItemOrderDetail;
import com.cams.inventorymanagement.application.dto.ProductOrderSummary;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.application.port.OrderRepository;
import com.cams.inventorymanagement.application.service.OrderService;
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
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
  private final UuidGenerator uuidGenerator;
  private final OrderRepository orderRepository;
  private final ProductService productService;

  @Transactional
  @Override
  public Order createOrder(List<ItemOrderDetail> itemOrderDetails) {
    if (itemOrderDetails == null || itemOrderDetails.isEmpty()) {
      throw new IllegalArgumentException("Item details cannot be null or empty.");
    }

    OrderId newOrderId = new OrderId(uuidGenerator.generate());
    log.info(
        "Creating order with ID: {} with {} items", newOrderId.value(), itemOrderDetails.size());

    List<OrderItem> orderItems = decrementStockAndGetOrderItems(itemOrderDetails, newOrderId);
    Order order = new Order(newOrderId, new OrderDate(localDateTime()), orderItems);
    orderRepository.save(order);

    log.info("Order created successfully with ID: {}", newOrderId.value());
    return order;
  }

  private List<OrderItem> decrementStockAndGetOrderItems(
      List<ItemOrderDetail> itemOrderDetails, OrderId orderId) {
    ArrayList<OrderItem> orderItems = new ArrayList<>();
    for (ItemOrderDetail itemOrderDetail : itemOrderDetails) {
      int quantity = itemOrderDetail.quantity();
      ProductId productId = new ProductId(itemOrderDetail.productId());
      Product product = productService.decrementStock(productId, quantity);
      UUID itemId = uuidGenerator.generate();
      OrderItem orderItem =
          new OrderItem(
              new ItemId(itemId),
              orderId,
              product.id(),
              new ItemQuantity(quantity),
              new ProductPrice(product.price().amount(), product.price().currency()));
      orderItems.add(orderItem);
    }
    return orderItems;
  }

  @Override
  @Transactional
  public Order updateOrderStatus(OrderId orderId, OrderStatus status) {
    if (status == null) {
      throw new IllegalArgumentException("Order status cannot be null.");
    }
    Order order = getOrderById(orderId);
    if (order.orderStatus().name().equals(status.name())) {
      log.info("Order status is already set to {} for order ID: {}", status, orderId.value());
      return order;
    }
    order.updateStatus(status, orderId.value());
    orderRepository.save(order);

    refillStockForCancelledOrder(orderId, status, order);

    log.info(
        "Order status updated successfully with status: {} for order ID: {}",
        status,
        orderId.value());
    return order;
  }

  private void refillStockForCancelledOrder(OrderId orderId, OrderStatus status, Order order) {
    if (status == OrderStatus.CANCELLED) {
      log.info("Add stocks back to products in order ID: {}", orderId.value());
      // If the order is cancelled, we need to increment the stock of the products in the order
      for (OrderItem orderItem : order.orderItems()) {
        productService.incrementStock(orderItem.productId(), orderItem.itemQuantity().value());
      }
      log.info("Product stocks incremented for order ID: {}", orderId.value());
    }
  }

  @Override
  public Order getOrderById(OrderId orderId) {
    if (orderId == null) {
      throw new IllegalArgumentException("Order ID cannot be null.");
    }
    log.info("Fetching order with ID: {}", orderId.value());
    Optional<Order> optionalOrder = orderRepository.findById(orderId.value());
    return optionalOrder.orElseThrow(
        () -> new RecordNotFoundException("Order does not exists with id: " + orderId.value()));
  }

  // Fetches all orders and computes the total value for each product across all COMPLETED/PENDING
  // orders
  // We can also use a database query filtering to improve performance incase of large datasets
  @Override
  public List<ProductOrderSummary> getOrderValueSummaryOfAllProducts() {
    log.info("Fetching orders for all products");
    List<Order> orders = orderRepository.findAll();

    // Efficient: use parallelStream for large datasets, direct return, and enum comparison
    return orders.parallelStream()
        .filter(order -> !order.isCancelled())
        .flatMap(order -> order.orderItems().stream())
        .collect(
            Collectors.groupingBy(
                orderItem -> orderItem.productId().value(),
                Collectors.reducing(BigDecimal.ZERO, OrderItem::computeItemTotal, BigDecimal::add)))
        .entrySet()
        .stream()
        .map(entry -> new ProductOrderSummary(entry.getKey(), entry.getValue()))
        .toList();
  }
}
