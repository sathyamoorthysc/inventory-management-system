package com.cams.inventorymanagement.adapter.persistence.jpa.repository;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductEntityRepository extends JpaRepository<ProductEntity, UUID> {
  boolean existsBySku(String sku);
}
