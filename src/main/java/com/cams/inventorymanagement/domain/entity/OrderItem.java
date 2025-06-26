package com.cams.inventorymanagement.domain.entity;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_ITEM_ID_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.ORDER_QUANTITY_VALUE_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_ID_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_PRICE_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidOrderItemException;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record OrderItem(
    ItemId itemId,
    OrderId orderId,
    ProductId productId,
    ItemQuantity itemQuantity,
    ProductPrice itemPrice) {

  public OrderItem {
    if (itemId == null) {
      throw new InvalidOrderItemException(ORDER_ITEM_ID_NULL);
    }
    if (productId == null) {
      throw new InvalidOrderItemException(PRODUCT_ID_NULL);
    }
    if (itemQuantity == null) {
      throw new InvalidOrderItemException(ORDER_QUANTITY_VALUE_NULL);
    }
    // itemPrice is the price of item at the time order item is created
    if (itemPrice == null) {
      throw new InvalidOrderItemException(PRODUCT_PRICE_NULL);
    }
  }

  public BigDecimal computeItemTotal() {
    BigDecimal itemQuantity = BigDecimal.valueOf(this.itemQuantity().value());
    BigDecimal unitPrice = this.itemPrice().amount();
    return itemQuantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
  }
}
