package com.cams.inventorymanagement.adapter.persistence.impl;

import com.cams.inventorymanagement.adapter.assembler.OrderDomainDbEntityAssembler;
import com.cams.inventorymanagement.adapter.assembler.OrderItemDomainDbEntityAssembler;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.OrderEntityRepository;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.requestscope.RequestScopedBean;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.application.port.OrderRepository;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the OrderRepository interface for managing Order entities. This class handles
 * the persistence of Order and OrderItem entities to the database.
 */
@Service
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {
  private final OrderEntityRepository orderEntityRepository;
  private final RequestScopedBean requestScopedBean;
  private final ProductEntityRepository productEntityRepository;

  @Override
  public void save(Order order) {
    List<OrderItemEntity> orderItems =
        order.orderItems().stream()
            .map(
                orderItem -> {
                  ProductEntity productEntity = getProductEntity(orderItem);
                  return OrderItemDomainDbEntityAssembler.toDbEntity(orderItem, productEntity);
                })
            .toList();

    OrderEntity orderEntity = OrderDomainDbEntityAssembler.toDbEntity(order, orderItems);
    orderEntityRepository.save(orderEntity);
  }

  // Retrieves the ProductEntity for the given OrderItem.
  // It first checks the request-scoped cache, and if not found, queries the database.
  private ProductEntity getProductEntity(OrderItem orderItem) {
    ProductEntity productEntity = null;
    if (requestScopedBean.getProductEntities() != null) {
      productEntity = requestScopedBean.getProductEntities().get(orderItem.productId().value());
    }
    if (productEntity == null) {
      productEntity =
          productEntityRepository
              .findById(orderItem.productId().value())
              .orElseThrow(
                  () ->
                      new RecordNotFoundException(
                          "Product not found with ID: " + orderItem.productId().value()));
    }
    return productEntity;
  }

  @Override
  public Optional<Order> findById(UUID orderId) {
    return orderEntityRepository
        .findById(orderId)
        .map(OrderDomainDbEntityAssembler::toDomainEntity);
  }

  @Override
  public List<Order> findAll() {
    List<OrderEntity> orderEntities = orderEntityRepository.findAll();
    return OrderDomainDbEntityAssembler.toDomainEntities(orderEntities);
  }
}
