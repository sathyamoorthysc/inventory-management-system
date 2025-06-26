package com.cams.inventorymanagement.application.port;

import com.cams.inventorymanagement.domain.entity.Order;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {
  void save(Order order);

  Optional<Order> findById(UUID orderId);

  List<Order> findAll();
}
