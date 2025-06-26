package com.cams.inventorymanagement.adapter.rest.controller;

import static com.cams.inventorymanagement.domain.valueobject.order.OrderStatus.CANCELLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cams.inventorymanagement.adapter.rest.dto.request.CreateOrderRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.UpdateOrderStatusRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.OrderResponse;
import com.cams.inventorymanagement.adapter.rest.service.OrderAdapterService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
  @Mock private OrderAdapterService orderAdapterService;
  @InjectMocks private OrderController orderController;

  @Test
  void shouldReturnResponseWhenOrderCreated() {
    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new CreateOrderRequest.OrderItemRequest(UUID.randomUUID(), 2)));
    when(orderAdapterService.createOrder(any())).thenReturn(mock(OrderResponse.class));

    ResponseEntity<OrderResponse> response = orderController.createOrder(request);

    verify(orderAdapterService).createOrder(any());
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }

  @Test
  void shouldReturnResponseWhenOrderStatusUpdated() {
    UpdateOrderStatusRequest request = new UpdateOrderStatusRequest(CANCELLED);

    when(orderAdapterService.updateOrderStatus(any(UUID.class), any()))
        .thenReturn(mock(OrderResponse.class));

    ResponseEntity<OrderResponse> response =
        orderController.updateOrderStatus(UUID.randomUUID(), request);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(orderAdapterService).updateOrderStatus(any(UUID.class), any());
  }

  @Test
  void shouldReturnResponseWhenOrderFound() {
    UUID orderId = UUID.randomUUID();
    when(orderAdapterService.getOrder(any(UUID.class))).thenReturn(mock(OrderResponse.class));

    ResponseEntity<OrderResponse> response = orderController.getOrder(orderId);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(orderAdapterService).getOrder(orderId);
  }
}
