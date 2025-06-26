package com.cams.inventorymanagement.adapter.persistence.jpa.entity;

import com.cams.inventorymanagement.adapter.persistence.jpa.embeddable.Price;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@Accessors(fluent = true)
public class OrderItemEntity {

  // Default constructor for JPA
  protected OrderItemEntity() {}

  public OrderItemEntity(UUID id, ProductEntity product, Integer quantity) {
    this.id = id;
    this.product = product;
    this.quantity = quantity;
    this.price = product.price();
  }

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "order_id", nullable = false)
  private OrderEntity order;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private ProductEntity product;

  @Embedded
  @Column(nullable = false)
  @AttributeOverrides({
    @AttributeOverride(
        name = "amount",
        column = @Column(precision = 12, scale = 2, nullable = false)),
    @AttributeOverride(name = "currency", column = @Column(length = 3, nullable = false))
  })
  private Price price;

  @Column(nullable = false)
  private Integer quantity;
}
