package com.cams.inventorymanagement.adapter.rest.service;

import com.cams.inventorymanagement.adapter.assembler.ProductDomainApiDtoAssembler;
import com.cams.inventorymanagement.adapter.rest.dto.request.AddStockRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateProductRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.ProductResponse;
import com.cams.inventorymanagement.application.service.ProductService;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductAdapterService {
  private final ProductService productService;

  public ProductResponse createProduct(CreateProductRequest request) {
    Product product =
        productService.createProduct(
            request.name(), request.sku(), request.price(), request.stock());
    return ProductDomainApiDtoAssembler.toApiResponse(product);
  }

  public List<ProductResponse> getProductsWithStockBelowThreshold(Integer stockThreshold) {
    List<Product> products = productService.getProductsWithStockBelowThreshold(stockThreshold);
    return ProductDomainApiDtoAssembler.toApiResponses(products);
  }

  public ProductResponse addProductStock(UUID productId, AddStockRequest request) {
    Product product = productService.incrementStock(new ProductId(productId), request.stockToAdd());
    return ProductDomainApiDtoAssembler.toApiResponse(product);
  }
}
