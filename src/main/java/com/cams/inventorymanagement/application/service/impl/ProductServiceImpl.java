package com.cams.inventorymanagement.application.service.impl;

import static com.cams.inventorymanagement.common.config.GlobalProperties.defaultCurrency;

import com.cams.inventorymanagement.application.exceptions.DuplicateSkuException;
import com.cams.inventorymanagement.application.exceptions.RecordNotFoundException;
import com.cams.inventorymanagement.application.port.ProductRepository;
import com.cams.inventorymanagement.application.service.ProductService;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
  private final ProductRepository productRepository;
  private final UuidGenerator uuidGenerator;

  @Override
  public Product createProduct(String name, String sku, BigDecimal price, int stock) {
    ProductSku productSku = new ProductSku(sku);
    if (productRepository.existsBySku(productSku.value())) {
      throw new DuplicateSkuException("Product SKU already exists: " + productSku.value());
    }

    log.info("Creating product with name {} and sku {}", name, sku);
    UUID newProductId = uuidGenerator.generate();
    Product product =
        new Product(
            new ProductId(newProductId),
            new ProductName(name),
            productSku,
            new ProductPrice(price, defaultCurrency()),
            new ProductStock(stock));

    productRepository.save(product);

    log.info(
        "Created product successfully with ID: {}, name: {}, sku: {}",
        product.id().value(),
        product.name().value(),
        product.sku().value());
    return product;
  }

  // This method retrieves all products and filters them based on the stock threshold using Java
  // Streams. It is not efficient for large datasets, and pagination or caching should be considered
  // for production use.
  @Override
  public List<Product> getProductsWithStockBelowThreshold(int stockThreshold) {
    if (stockThreshold <= 0) {
      throw new IllegalArgumentException("Stock threshold must be greater than zero.");
    }

    log.info("Fetching products with stock below threshold: {}", stockThreshold);
    List<Product> products = productRepository.findAll();
    List<Product> filteredProducts = List.of();
    if (products != null && !products.isEmpty()) {
      // we can use parallel stream for better performance if the list is large
      filteredProducts =
          products.stream().filter(product -> product.stock().value() < stockThreshold).toList();
    }
    log.info(
        "Found {} products with stock below threshold: {}",
        filteredProducts.size(),
        stockThreshold);
    return filteredProducts;
  }

  private Product getProductById(ProductId productId) {
    if (productId == null) {
      throw new IllegalArgumentException("Product ID must not be null.");
    }

    Optional<Product> optionalProduct = productRepository.findById(productId.value());
    return optionalProduct.orElseThrow(
        () -> new RecordNotFoundException("Product does not exists with id: " + productId.value()));
  }

  @Override
  public Product decrementStock(ProductId productId, int stockToDecrement) {
    Product product = getProductById(productId);
    product.decrementStock(stockToDecrement);
    productRepository.save(product);
    log.info(
        "Decremented stock for product ID: {} by {}, new stock: {}",
        product.id().value(),
        stockToDecrement,
        product.stock().value());
    return product;
  }

  @Override
  public Product incrementStock(ProductId productId, int stockToIncrement) {
    Product product = getProductById(productId);
    product.incrementStock(stockToIncrement);
    productRepository.save(product);
    log.info(
        "Incremented stock for product ID: {} by {}, new stock: {}",
        product.id().value(),
        stockToIncrement,
        product.stock().value());
    return product;
  }
}
