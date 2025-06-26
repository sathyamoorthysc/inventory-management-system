package com.cams.inventorymanagement.application.service;

import com.cams.inventorymanagement.application.dto.ItemOrderDetail;
import com.cams.inventorymanagement.application.dto.ProductOrderSummary;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import java.util.List;

public interface OrderService {
  Order createOrder(List<ItemOrderDetail> itemOrderDetails);

  Order updateOrderStatus(OrderId orderId, OrderStatus orderStatus);

  Order getOrderById(OrderId orderId);

  List<ProductOrderSummary> getOrderValueSummaryOfAllProducts();
}
