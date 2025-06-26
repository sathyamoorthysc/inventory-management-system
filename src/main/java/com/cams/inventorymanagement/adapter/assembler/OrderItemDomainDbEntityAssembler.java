package com.cams.inventorymanagement.adapter.assembler;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import java.util.Currency;

/** Assembler class to convert between OrderItem Domain objects and Database entities. */
public class OrderItemDomainDbEntityAssembler {
  private OrderItemDomainDbEntityAssembler() {}

  public static OrderItemEntity toDbEntity(OrderItem orderItem, ProductEntity productEntity) {
    return new OrderItemEntity(
        orderItem.itemId().value(), productEntity, orderItem.itemQuantity().value());
  }

  public static OrderItem toDomainEntity(OrderItemEntity orderItemEntity) {
    return new OrderItem(
        new ItemId(orderItemEntity.id()),
        new OrderId(orderItemEntity.order().id()),
        new ProductId(orderItemEntity.product().id()),
        new ItemQuantity(orderItemEntity.quantity()),
        new ProductPrice(
            orderItemEntity.price().amount(),
            Currency.getInstance(orderItemEntity.price().currency())));
  }
}
