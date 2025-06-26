package com.cams.inventorymanagement.adapter.persistence.impl;

import com.cams.inventorymanagement.adapter.assembler.ProductDomainDbEntityAssembler;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.requestscope.RequestScopedBean;
import com.cams.inventorymanagement.application.port.ProductRepository;
import com.cams.inventorymanagement.domain.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the ProductRepository interface for managing Product entities. This class
 * handles the persistence of Product entities to the database.
 */
@Service
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
  private final ProductEntityRepository productEntityRepository;
  private final RequestScopedBean requestScopedBean;

  @Override
  public void save(Product product) {
    ProductEntity dbEntity = ProductDomainDbEntityAssembler.toDbEntity(product);
    productEntityRepository.save(dbEntity);
  }

  @Override
  public boolean existsBySku(String sku) {
    return productEntityRepository.existsBySku(sku);
  }

  @Override
  public List<Product> findAll() {
    List<ProductEntity> productEntities = productEntityRepository.findAll();
    return ProductDomainDbEntityAssembler.toDomainEntities(productEntities);
  }

  @Override
  public Optional<Product> findById(UUID productId) {
    return productEntityRepository
        .findById(productId)
        .map(
            entity -> {
              requestScopedBean.addProductEntity(entity);
              return ProductDomainDbEntityAssembler.toDomainEntity(entity);
            });
  }
}
