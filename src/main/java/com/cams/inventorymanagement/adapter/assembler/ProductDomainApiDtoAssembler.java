package com.cams.inventorymanagement.adapter.assembler;

import com.cams.inventorymanagement.adapter.rest.dto.response.PriceResponse;
import com.cams.inventorymanagement.adapter.rest.dto.response.ProductResponse;
import com.cams.inventorymanagement.domain.entity.Product;
import java.util.List;

/** Assembler to convert between Product API DTOs and Product Domain objects. */
public class ProductDomainApiDtoAssembler {
  private ProductDomainApiDtoAssembler() {}

  public static ProductResponse toApiResponse(Product product) {
    return new ProductResponse(
        product.id().value(),
        product.name().value(),
        product.sku().value(),
        product.stock().value(),
        new PriceResponse(product.price().amount(), product.price().currency().getCurrencyCode()));
  }

  public static List<ProductResponse> toApiResponses(List<Product> products) {
    return products.stream().map(ProductDomainApiDtoAssembler::toApiResponse).toList();
  }
}
