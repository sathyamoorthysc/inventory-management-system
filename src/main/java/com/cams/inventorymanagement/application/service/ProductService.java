package com.cams.inventorymanagement.application.service;

import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
  Product createProduct(String name, String sku, BigDecimal price, int stock);

  List<Product> getProductsWithStockBelowThreshold(int stockThreshold);

  Product decrementStock(ProductId productId, int stockToDecrement);

  Product incrementStock(ProductId productId, int stockThreshold);
}
