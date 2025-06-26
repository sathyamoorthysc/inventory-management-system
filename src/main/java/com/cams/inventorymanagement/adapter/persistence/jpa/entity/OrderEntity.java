package com.cams.inventorymanagement.adapter.persistence.jpa.entity;

import com.cams.inventorymanagement.domain.valueobject.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Accessors(fluent = true)
public class OrderEntity {

  // Default constructor for JPA
  protected OrderEntity() {}

  public OrderEntity(
      UUID id,
      LocalDateTime orderDate,
      List<OrderItemEntity> items,
      OrderStatus status,
      Long version) {
    this.id = id;
    this.orderDate = orderDate;
    this.items = items;
    this.status = status;
    this.version = version;
  }

  @Id private UUID id;

  @Column(nullable = false)
  private LocalDateTime orderDate;

  @OneToMany(
      mappedBy = "order",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true)
  private List<OrderItemEntity> items;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrderStatus status;

  @Version private Long version;
}
