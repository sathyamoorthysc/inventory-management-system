package com.cams.inventorymanagement.application.port;

import com.cams.inventorymanagement.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {
  void save(Product product);

  List<Product> findAll();

  boolean existsBySku(String sku);

  Optional<Product> findById(UUID productId);
}
