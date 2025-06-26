package com.cams.inventorymanagement.adapter.rest.dto.requestscope;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/** A request-scoped bean that holds product entities for the duration of a single HTTP request. */
@Getter
@Component
@RequestScope
public class RequestScopedBean {
  private Map<UUID, ProductEntity> productEntities;

  public void addProductEntity(ProductEntity productEntity) {
    if (this.productEntities == null) {
      this.productEntities = new HashMap<>();
    }
    this.productEntities.put(productEntity.id(), productEntity);
  }
}
