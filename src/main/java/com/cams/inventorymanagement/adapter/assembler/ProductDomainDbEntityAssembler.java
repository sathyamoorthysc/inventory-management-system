package com.cams.inventorymanagement.adapter.assembler;

import com.cams.inventorymanagement.adapter.persistence.jpa.embeddable.Price;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.util.Currency;
import java.util.List;

/** Assembler class to convert between Product Domain objects and Database entities. */
public class ProductDomainDbEntityAssembler {
  private ProductDomainDbEntityAssembler() {}

  public static ProductEntity toDbEntity(Product product) {
    return new ProductEntity(
        product.id().value(),
        product.name().value(),
        product.sku().value(),
        new Price(product.price().amount(), product.price().currency().getCurrencyCode()),
        product.stock().value(),
        product.version());
  }

  public static Product toDomainEntity(ProductEntity productEntity) {
    Product product =
        new Product(
            new ProductId(productEntity.id()),
            new ProductName(productEntity.name()),
            new ProductSku(productEntity.sku()),
            new ProductPrice(
                productEntity.price().amount(),
                Currency.getInstance(productEntity.price().currency())),
            new ProductStock(productEntity.stock()));
    product.version(productEntity.version());
    return product;
  }

  public static List<Product> toDomainEntities(List<ProductEntity> productEntities) {
    return productEntities.stream().map(ProductDomainDbEntityAssembler::toDomainEntity).toList();
  }
}
