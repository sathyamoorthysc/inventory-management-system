package com.cams.inventorymanagement.common.config;

import static java.time.Clock.systemUTC;

import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {
  @Bean
  Clock clock() {
    return systemUTC();
  }

  public static LocalDateTime localDateTime() {
    return LocalDateTime.now(systemUTC());
  }
}
