package com.cams.inventorymanagement.adapter.assembler;

import com.cams.inventorymanagement.adapter.rest.dto.response.OrderResponse;
import com.cams.inventorymanagement.adapter.rest.dto.response.PriceResponse;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;

/** Assembler to convert between Order API DTOs and Order Domain objects. */
public class OrderDomainApiDtoAssembler {
  private OrderDomainApiDtoAssembler() {}

  public static OrderResponse toApiResponse(Order order) {
    return new OrderResponse(
        order.orderId().value(),
        order.orderDate().value(),
        order.orderItems().stream()
            .map(
                item ->
                    new OrderResponse.OrderItemResponse(
                        item.productId().value(),
                        item.itemQuantity().value(),
                        getPriceResponse(item)))
            .toList(),
        order.orderStatus().name(),
        order.computeAndGetTotalPrice().toPlainString());
  }

  private static PriceResponse getPriceResponse(OrderItem item) {
    return new PriceResponse(
        item.itemPrice().amount(), item.itemPrice().currency().getCurrencyCode());
  }
}
