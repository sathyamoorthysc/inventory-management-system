package com.cams.inventorymanagement.adapter.persistence.jpa.entity;

import com.cams.inventorymanagement.adapter.persistence.jpa.embeddable.Price;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "products")
@Getter
@Setter
@Accessors(fluent = true)
public class ProductEntity {

  // Default constructor for JPA
  protected ProductEntity() {}

  public ProductEntity(UUID id, String name, String sku, Price price, Integer stock, Long version) {
    this.id = id;
    this.name = name;
    this.sku = sku;
    this.price = price;
    this.stock = stock;
    this.version = version;
  }

  @Id private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String sku;

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
  private Integer stock;

  @Version private Long version;
}
