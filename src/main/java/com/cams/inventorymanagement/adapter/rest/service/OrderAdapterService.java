package com.cams.inventorymanagement.adapter.rest.service;

import com.cams.inventorymanagement.adapter.assembler.OrderDomainApiDtoAssembler;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateOrderRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.UpdateOrderStatusRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.OrderResponse;
import com.cams.inventorymanagement.application.dto.ItemOrderDetail;
import com.cams.inventorymanagement.application.dto.ProductOrderSummary;
import com.cams.inventorymanagement.application.service.OrderService;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAdapterService {
  private final OrderService orderService;

  public OrderResponse createOrder(CreateOrderRequest request) {
    List<ItemOrderDetail> itemOrderDetails =
        request.items().stream()
            .map(item -> new ItemOrderDetail(item.productId(), item.quantity()))
            .toList();
    Order order = orderService.createOrder(itemOrderDetails);
    return OrderDomainApiDtoAssembler.toApiResponse(order);
  }

  public OrderResponse updateOrderStatus(UUID orderId, UpdateOrderStatusRequest request) {
    Order order = orderService.updateOrderStatus(new OrderId(orderId), request.status());
    return OrderDomainApiDtoAssembler.toApiResponse(order);
  }

  public OrderResponse getOrder(UUID orderId) {
    Order order = orderService.getOrderById(new OrderId(orderId));
    return OrderDomainApiDtoAssembler.toApiResponse(order);
  }

  public List<ProductOrderSummary> getOrderSummaryOfAllProducts() {
    return orderService.getOrderValueSummaryOfAllProducts();
  }
}
