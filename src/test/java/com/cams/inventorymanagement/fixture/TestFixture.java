package com.cams.inventorymanagement.fixture;

import com.cams.inventorymanagement.adapter.persistence.jpa.embeddable.Price;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.OrderItemEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.domain.entity.Order;
import com.cams.inventorymanagement.domain.entity.OrderItem;
import com.cams.inventorymanagement.domain.entity.Product;
import com.cams.inventorymanagement.domain.valueobject.order.OrderDate;
import com.cams.inventorymanagement.domain.valueobject.order.OrderId;
import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemId;
import com.cams.inventorymanagement.domain.valueobject.orderitem.ItemQuantity;
import com.cams.inventorymanagement.domain.valueobject.product.ProductId;
import com.cams.inventorymanagement.domain.valueobject.product.ProductName;
import com.cams.inventorymanagement.domain.valueobject.product.ProductPrice;
import com.cams.inventorymanagement.domain.valueobject.product.ProductSku;
import com.cams.inventorymanagement.domain.valueobject.product.ProductStock;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

public class TestFixture {
  public static ProductEntityBuilder productEntityBuilder() {
    return new ProductEntityBuilder();
  }

  public static OrderItemEntityBuilder orderItemEntityBuilder() {
    return new OrderItemEntityBuilder();
  }

  public static OrderEntityBuilder orderEntityBuilder() {
    return new OrderEntityBuilder();
  }

  public static OrderBuilder orderBuilder() {
    return new OrderBuilder();
  }

  public static OrderItemBuilder orderItemBuilder() {
    return new OrderItemBuilder();
  }

  public static ProductBuilder productBuilder() {
    return new ProductBuilder();
  }

  public static class ProductEntityBuilder {
    private UUID id = UUID.randomUUID();
    private String name = "Test Product";
    private String sku = "SKU-001";
    private Price price = new Price(BigDecimal.TEN, "INR");
    private Integer stock = 10;
    private Long version = null;

    public ProductEntityBuilder id(UUID id) {
      this.id = id;
      return this;
    }

    public ProductEntityBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProductEntityBuilder sku(String sku) {
      this.sku = sku;
      return this;
    }

    public ProductEntityBuilder stock(Integer stock) {
      this.stock = stock;
      return this;
    }

    public ProductEntityBuilder version(Long version) {
      this.version = version;
      return this;
    }

    public ProductEntity build() {
      return new ProductEntity(id, name, sku, price, stock, version);
    }
  }

  public static class OrderItemEntityBuilder {
    private UUID id = UUID.randomUUID();
    private ProductEntity product;
    private Integer quantity = 2;

    public OrderItemEntityBuilder id(UUID id) {
      this.id = id;
      return this;
    }

    public OrderItemEntityBuilder product(ProductEntity product) {
      this.product = product;
      return this;
    }

    public OrderItemEntityBuilder order(OrderEntity order) {
      this.order(order);
      return this;
    }

    public OrderItemEntity build() {
      return new OrderItemEntity(id, product, quantity);
    }
  }

  public static class OrderEntityBuilder {
    private UUID id = UUID.randomUUID();
    private LocalDateTime orderDate = LocalDateTime.now();
    private List<OrderItemEntity> items = List.of();
    private OrderStatus status = OrderStatus.PENDING;
    private Long version = null;

    public OrderEntityBuilder id(UUID id) {
      this.id = id;
      return this;
    }

    public OrderEntityBuilder items(List<OrderItemEntity> items) {
      this.items = items;
      return this;
    }

    public OrderEntityBuilder version(Long version) {
      this.version = version;
      return this;
    }

    public OrderEntity build() {
      return new OrderEntity(id, orderDate, items, status, version);
    }
  }

  public static class OrderBuilder {
    private OrderId orderId = new OrderId(UUID.randomUUID());
    private OrderDate orderDate = new OrderDate(LocalDateTime.now());
    private List<OrderItem> items = List.of();
    private OrderStatus status = OrderStatus.PENDING;

    public OrderBuilder id(OrderId orderId) {
      this.orderId = orderId;
      return this;
    }

    public OrderBuilder items(List<OrderItem> items) {
      this.items = items;
      return this;
    }

    public Order build() {
      return new Order(orderId, orderDate, items);
    }
  }

  public static class OrderItemBuilder {
    private ItemId itemId = new ItemId(UUID.randomUUID());
    private OrderId orderId = new OrderId(UUID.randomUUID());
    private ProductId productId = new ProductId(UUID.randomUUID());
    private ItemQuantity quantity = new ItemQuantity(2);
    private ProductPrice price =
        new ProductPrice(BigDecimal.valueOf(100.0), Currency.getInstance("INR"));

    public OrderItemBuilder productId(ProductId productId) {
      this.productId = productId;
      return this;
    }

    public OrderItem build() {
      return new OrderItem(itemId, orderId, productId, quantity, price);
    }
  }

  public static class ProductBuilder {
    private ProductId productId = new ProductId(UUID.randomUUID());
    private ProductName productName = new ProductName("Test Product");
    private ProductSku productSku = new ProductSku("SKU-001");
    private ProductPrice productPrice =
        new ProductPrice(BigDecimal.TEN, Currency.getInstance("INR"));
    private ProductStock productStock = new ProductStock(10);

    public ProductBuilder productId(ProductId productId) {
      this.productId = productId;
      return this;
    }

    public Product build() {
      return new Product(productId, productName, productSku, productPrice, productStock);
    }
  }
}
