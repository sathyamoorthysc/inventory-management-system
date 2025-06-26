package com.cams.inventorymanagement.domain.valueobject.product;

import static com.cams.inventorymanagement.domain.exceptions.ExceptionMessages.PRODUCT_STOCK_VALUE_NEGATIVE;

import com.cams.inventorymanagement.domain.exceptions.InsufficientStockException;
import com.cams.inventorymanagement.domain.exceptions.InvalidProductException;
import java.util.UUID;

public record ProductStock(int value) {
  private static final int MIN_STOCK = 0;

  public ProductStock {
    if (value < MIN_STOCK) {
      throw new InvalidProductException(PRODUCT_STOCK_VALUE_NEGATIVE);
    }
  }

  public ProductStock decrement(int stockToDecrement, UUID productId) {
    if (stockToDecrement <= 0) {
      throw new IllegalArgumentException(
          "Stock to decrement must be non-negative or zero for product ID: "
              + productId.toString());
    }
    if (value < stockToDecrement) {
      throw new InsufficientStockException(
          "Insufficient stock available for product ID: "
              + productId.toString()
              + ". Current stock: "
              + value
              + ", requested: "
              + stockToDecrement);
    }
    return new ProductStock(this.value - stockToDecrement);
  }

  public ProductStock increment(int stockToIncrement, UUID productId) {
    if (stockToIncrement <= 0) {
      throw new IllegalArgumentException(
          "Stock to increment must be non-negative or zero for product ID: "
              + productId.toString());
    }
    return new ProductStock(this.value + stockToIncrement);
  }
}
