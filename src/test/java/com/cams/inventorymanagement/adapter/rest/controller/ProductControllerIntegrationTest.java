package com.cams.inventorymanagement.adapter.rest.controller;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateProductRequest;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private ProductEntityRepository productEntityRepository;
  @MockitoBean UuidGenerator uuidGenerator;

  @BeforeEach
  void setUp() {
    when(uuidGenerator.generate())
        .thenReturn(UUID.fromString("08b31c2a-6488-4659-a8f2-1fa6f74eaee8"));
  }

  @Test
  void shouldCreateProductSuccessfully() throws Exception {
    CreateProductRequest request =
        new CreateProductRequest("How to Guide", "SKU999", new BigDecimal("10.549"), 5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(
            content()
                .json(
                    """

                            {
              "id": "08b31c2a-6488-4659-a8f2-1fa6f74eaee8",
              "name": "How to Guide",
              "sku": "SKU999",
              "stock": 5,
              "unitPrice": {
                "amount": "10.55",
                "currency": "INR"
              }
            }
            """));
  }

  @Test
  void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
    CreateProductRequest request =
        new CreateProductRequest("", "SKU123", new BigDecimal("10.00"), 5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(
            content()
                .json(
                    """
            {
              "error": "name: Product name is required",
              "path": "/v1/products",
              "status": "Bad Request",
              "refId": "08b31c2a-6488-4659-a8f2-1fa6f74eaee8"
            }
            """));
  }

  @Test
  void shouldReturnBadRequestWhenSkuIsBlank() throws Exception {
    CreateProductRequest request = new CreateProductRequest("Book", "", new BigDecimal("10.00"), 5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("sku: Product SKU is required")));
  }

  @Test
  void shouldReturnBadRequestWhenAmountIsNull() throws Exception {
    CreateProductRequest request = new CreateProductRequest("Book", "SKU123", null, 5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("price: Product price is required")));
  }

  @Test
  void shouldReturnBadRequestWhenStockIsNull() throws Exception {
    CreateProductRequest request =
        new CreateProductRequest("Book", "SKU123", new BigDecimal("10.00"), null);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("stock: Product stock is required")));
  }

  @Test
  void shouldReturnBadRequestWhenSkuIsDuplicate() throws Exception {
    String duplicateSku = "DUPSKU123";
    CreateProductRequest firstRequest =
        new CreateProductRequest("I am Book1", duplicateSku, new BigDecimal("15.00"), 10);
    CreateProductRequest secondRequest =
        new CreateProductRequest("I am Book2", duplicateSku, new BigDecimal("20.00"), 8);

    when(uuidGenerator.generate())
        .thenReturn(UUID.fromString("d4f5e6b7-8c9d-4e0f-a1b2-c3d4e5f6a7b8"))
        .thenReturn(UUID.fromString("55f6e7b8-9c0d-4e1f-a2b3-c4d5e6f7a8b9"))
        .thenReturn(UUID.fromString("01234567-89ab-cdef-0123-456789abcdef"));

    // First creation should succeed
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is("55f6e7b8-9c0d-4e1f-a2b3-c4d5e6f7a8b9")));

    // Second creation with same SKU should fail
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error", is("Product SKU already exists: DUPSKU123")))
        .andExpect(jsonPath("$.refId", is("01234567-89ab-cdef-0123-456789abcdef")));
  }

  @Test
  void shouldReturnBadRequestWhenStockIsNegative() throws Exception {
    CreateProductRequest request =
        new CreateProductRequest("English Book", "SKU_NEG_STOCK", new BigDecimal("10.00"), -5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("ProductStock value cannot be negative")));
  }

  @Test
  void shouldReturnBadRequestWhenAmountIsNegative() throws Exception {
    CreateProductRequest request =
        new CreateProductRequest("Science Book", "SKU_NEG_AMOUNT", new BigDecimal("-10.00"), 5);
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("ProductPrice value cannot be null or negative")));
  }

  @Test
  void shouldReturnProductsWithStockBelowThreshold() throws Exception {
    CreateProductRequest p1 =
        new CreateProductRequest("Book A Theory", "SKU_A", new BigDecimal("10.00"), 2);
    CreateProductRequest p2 =
        new CreateProductRequest("Book B Theory", "SKU_B", new BigDecimal("15.00"), 5);
    CreateProductRequest p3 =
        new CreateProductRequest("Book C Theory", "SKU_C", new BigDecimal("20.00"), 8);

    when(uuidGenerator.generate()).thenAnswer((t) -> UUID.randomUUID());

    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p1)))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p2)))
        .andExpect(status().isCreated());
    mockMvc
        .perform(
            post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p3)))
        .andExpect(status().isCreated());

    // Query for products with stock less than 6
    mockMvc
        .perform(get("/v1/products/stock").param("stockLt", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].name", anyOf(is("Book A Theory"), is("Book B Theory"))))
        .andExpect(jsonPath("$[1].name", anyOf(is("Book A Theory"), is("Book B Theory"))));
  }

  @Test
  void shouldReturnEmptyListWhenNoProductsWithStockBelowThreshold() throws Exception {
    when(uuidGenerator.generate()).thenAnswer((t) -> UUID.randomUUID());

    mockMvc
        .perform(get("/v1/products/stock").param("stockLt", "6"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)))
        .andExpect(content().json("[]"));
  }

  @AfterEach
  void tearDown() {
    productEntityRepository.deleteAll();
  }
}
