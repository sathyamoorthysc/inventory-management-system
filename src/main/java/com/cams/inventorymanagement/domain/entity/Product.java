package com.cams.inventorymanagement.domain.entity;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_ID_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_NAME_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_PRICE_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_SKU_NULL;
import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_STOCK_NULL;

import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.util.Objects;

// Using class because product data are mutated frequently, such as stock updates.
// Not using lombok to keep domain logic independent of external libraries. But we can use it based
// on project standards that reduce boilerplate codes.

public class Product {
  private final ProductId id;
  private final ProductName name;
  private final ProductSku sku;
  private final ProductPrice price;
  private ProductStock stock;

  private Long version;

  public Product(
      ProductId id, ProductName name, ProductSku sku, ProductPrice price, ProductStock stock) {
    if (id == null) {
      throw new InvalidProductException(PRODUCT_ID_NULL);
    }
    if (name == null) {
      throw new InvalidProductException(PRODUCT_NAME_NULL);
    }
    if (sku == null) {
      throw new InvalidProductException(PRODUCT_SKU_NULL);
    }
    if (price == null) {
      throw new InvalidProductException(PRODUCT_PRICE_NULL);
    }
    if (stock == null) {
      throw new InvalidProductException(PRODUCT_STOCK_NULL);
    }
    this.id = id;
    this.name = name;
    this.sku = sku;
    this.price = price;
    this.stock = stock;
  }

  public ProductId id() {
    return this.id;
  }

  public ProductName name() {
    return this.name;
  }

  public ProductSku sku() {
    return this.sku;
  }

  public ProductPrice price() {
    return this.price;
  }

  public ProductStock stock() {
    return this.stock;
  }

  public void decrementStock(int stockToDecrement) {
    this.stock = this.stock.decrement(stockToDecrement, this.id.value());
  }

  public void incrementStock(int stockToIncrement) {
    this.stock = this.stock.increment(stockToIncrement, this.id.value());
  }

  public Long version() {
    return this.version;
  }

  public void version(Long version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Product product = (Product) o;
    return Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
