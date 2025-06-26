package com.cams.inventorymanagement.adapter.rest.exceptionhandler;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateOrderRequest;
import com.cams.inventorymanagement.adapter.rest.service.OrderAdapterService;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.cams.inventorymanagement.fixture.TestFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerIntegrationTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockitoSpyBean OrderAdapterService orderAdapterService;
  @MockitoBean UuidGenerator uuidGenerator;
  @Autowired ProductEntityRepository productRepository;
  private UUID validProductId = UUID.fromString("222e4567-e89b-12d3-a456-426614174000");

  @BeforeEach
  void setUp() {
    ProductEntity product =
        TestFixture.productEntityBuilder().id(validProductId).version(null).build();
    productRepository.save(product);
    when(uuidGenerator.generate()).thenReturn(UUID.randomUUID());
  }

  @Test
  void shouldReturnBadRequestForInvalidJson() throws Exception {
    String invalidJson = "{\"invalid\": }";
    mockMvc
        .perform(post("/v1/orders").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error",
                is("JSON parse error: Unexpected character ('}' (code 125)): expected a value")));
  }

  @Test
  void shouldReturnBadRequestForIllegalArgument() throws Exception {
    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new CreateOrderRequest.OrderItemRequest(validProductId, -1)));
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error",
                is(
                    "Stock to decrement must be non-negative or zero "
                        + "for product ID: 222e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void shouldReturnNotFoundForNonExistentOrder() throws Exception {
    UUID nonExistentOrderId = UUID.fromString("999e4567-e89b-12d3-a456-426614174000");
    mockMvc
        .perform(get("/v1/orders/" + nonExistentOrderId))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.error",
                is("Order does not exists with id: 999e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void shouldReturnBadRequestForDomainValidationException() throws Exception {
    CreateOrderRequest request = new CreateOrderRequest(List.of());
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("Item details cannot be null or empty.")));
  }

  @Test
  void shouldReturnInternalServerErrorForUnhandledException() throws Exception {
    doThrow(new RuntimeException("Unexpected error"))
        .when(orderAdapterService)
        .getOrder(any(UUID.class));
    mockMvc
        .perform(get("/v1/orders/c9f3e2d6-6b9e-4c71-b6fd-d3f94370df8a"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error", is("Unexpected error")));
  }

  @AfterEach
  void tearDown() {
    productRepository.deleteAll();
  }
}
