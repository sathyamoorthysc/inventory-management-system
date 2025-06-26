package com.cams.inventorymanagement.adapter.persistence.jpa.embeddable;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
@Embeddable
public class Price {
  private BigDecimal amount;

  private String currency;

  // Default constructor for JPA
  protected Price() {}

  public BigDecimal amount() {
    return amount.setScale(2, RoundingMode.HALF_UP);
  }
}
