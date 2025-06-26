package com.cams.inventorymanagement.common.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidGenerator {
  public UUID generate() {
    return UUID.randomUUID();
  }
}
