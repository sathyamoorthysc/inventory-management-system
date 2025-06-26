package com.cams.inventorymanagement.adapter.assembler;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import com.cams.inventorymanagement.domain.valueobject.order.OrderDate;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import java.util.List;

/** Assembler class to convert between Order Domain objects and Database entities. */
public class OrderDomainDbEntityAssembler {
  private OrderDomainDbEntityAssembler() {}

  public static OrderEntity toDbEntity(Order order, List<OrderItemEntity> orderItems) {
    OrderEntity orderEntity =
        new OrderEntity(
            order.orderId().value(),
            order.orderDate().value(),
            orderItems,
            order.orderStatus(),
            order.version());
    orderItems.forEach(orderItem -> orderItem.order(orderEntity));
    return orderEntity;
  }

  public static Order toDomainEntity(OrderEntity orderEntity) {
    List<OrderItem> orderItems =
        orderEntity.items().stream().map(OrderItemDomainDbEntityAssembler::toDomainEntity).toList();

    Order order =
        new Order(
            new OrderId(orderEntity.id()), new OrderDate(orderEntity.orderDate()), orderItems);
    order.version(orderEntity.version());
    return order;
  }

  public static List<Order> toDomainEntities(List<OrderEntity> orderEntities) {
    return orderEntities.stream().map(OrderDomainDbEntityAssembler::toDomainEntity).toList();
  }
}
