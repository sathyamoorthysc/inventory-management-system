package com.cams.inventorymanagement.adapter.rest.controller;

import static com.cams.inventorymanagement.domain.valueobject.order.OrderStatus.CANCELLED;
import static com.cams.inventorymanagement.domain.valueobject.order.OrderStatus.COMPLETED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cams.inventorymanagement.adapter.persistence.jpa.entity.ProductEntity;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.OrderEntityRepository;
import com.cams.inventorymanagement.adapter.persistence.jpa.repository.ProductEntityRepository;
import com.cams.inventorymanagement.adapter.rest.dto.request.CreateOrderRequest;
import com.cams.inventorymanagement.adapter.rest.dto.request.UpdateOrderStatusRequest;
import com.cams.inventorymanagement.adapter.rest.dto.response.OrderResponse;
import com.cams.inventorymanagement.adapter.rest.service.OrderAdapterService;
import com.cams.inventorymanagement.common.util.UuidGenerator;
import com.cams.inventorymanagement.fixture.TestFixture;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(OutputCaptureExtension.class)
class OrderControllerIntegrationTest {
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired ProductEntityRepository productRepository;
  @Autowired OrderEntityRepository orderEntityRepository;
  @MockitoSpyBean OrderAdapterService orderAdapterService;
  @MockitoBean UuidGenerator uuidGenerator;
  private static MockedStatic<LocalDateTime> mockedStaticOfInstant = null;
  private final UUID validProductId = UUID.fromString("222e4567-e89b-12d3-a456-426614174000");

  @BeforeAll
  static void setup() {
    if (mockedStaticOfInstant != null) {
      mockedStaticOfInstant.close();
    }
    mockedStaticOfInstant = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
    LocalDateTime fixedDateTime = LocalDateTime.parse("2025-06-25T19:40:28.70219");
    mockedStaticOfInstant
        .when(() -> LocalDateTime.now(any(Clock.class)))
        .thenAnswer(invocation -> fixedDateTime);
  }

  @BeforeEach
  void setUp() {
    ProductEntity product =
        TestFixture.productEntityBuilder().id(validProductId).version(null).build();
    productRepository.save(product);
    when(uuidGenerator.generate()).thenReturn(UUID.randomUUID());
  }

  @Test
  void shouldCreateOrderWhenValidRequestProvided() throws Exception {
    CreateOrderRequest request =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(validProductId, 2)));
    UUID orderId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    when(uuidGenerator.generate()).thenReturn(orderId);
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(
            content()
                .json(
                    """
                      {
                        "id": "123e4567-e89b-12d3-a456-426614174000",
                        "date": "2025-06-25T19:40:28.70219",
                        "items": [
                          {
                            "productId": "222e4567-e89b-12d3-a456-426614174000",
                            "quantity": 2,
                            "unitPrice": {
                              "amount": "10.00",
                              "currency": "INR"
                            }
                          }
                        ],
                        "status": "PENDING",
                        "totalPrice": "20.00"
                      }
                      """));
  }

  @Test
  void shouldReturnBadRequestWhenExcessStockProvided() throws Exception {
    CreateOrderRequest request =
        new CreateOrderRequest(
            List.of(new CreateOrderRequest.OrderItemRequest(validProductId, 100)));
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
                    "Insufficient stock available for "
                        + "product ID: 222e4567-e89b-12d3-a456-426614174000. "
                        + "Current stock: 10, requested: 100")));
  }

  @Test
  void shouldReturnNotFoundWhenInvalidProductProvided() throws Exception {
    UUID productId = UUID.fromString("ed0b3c5b-d443-4470-a5e9-871aaa5d0203");
    CreateOrderRequest request =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(productId, 100)));
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.error",
                is("Product does not exists with id: ed0b3c5b-d443-4470-a5e9-871aaa5d0203")));
  }

  @Test
  void shouldReturnBadRequestWhenInvalidQuantityProvided() throws Exception {
    CreateOrderRequest request =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(validProductId, 0)));
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
                    "Stock to decrement must be non-negative or zero for "
                        + "product ID: 222e4567-e89b-12d3-a456-426614174000")));
  }

  @Test
  void shouldUpdateOrderStatusWhenValidRequestProvided() throws Exception {
    // First create an order
    CreateOrderRequest request =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(validProductId, 1)));
    String response =
        mockMvc
            .perform(
                post("/v1/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andReturn()
            .getResponse()
            .getContentAsString();
    OrderResponse order = objectMapper.readValue(response, OrderResponse.class);

    UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(COMPLETED);
    mockMvc
        .perform(
            patch("/v1/orders/" + order.id() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status", is("COMPLETED")));
  }

  @Test
  void shouldReturnBadRequestWhenNullStatusProvided() throws Exception {
    UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(null);

    mockMvc
        .perform(
            patch("/v1/orders/" + UUID.randomUUID() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", is("status: must not be null")));
  }

  @Test
  void shouldReturnNotFoundWhenUpdatingStatusOfNonExistentOrder() throws Exception {
    UUID orderId = UUID.fromString("5c1be4ff-d17a-4565-a2a7-682661e09f0c");
    UpdateOrderStatusRequest statusRequest = new UpdateOrderStatusRequest(CANCELLED);
    mockMvc
        .perform(
            patch("/v1/orders/" + orderId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.error",
                is("Order does not exists with id: 5c1be4ff-d17a-4565-a2a7-682661e09f0c")));
  }

  @Test
  void shouldRetrieveOrderWhenValidIdProvided() throws Exception {
    UUID orderId = UUID.fromString("dddd4567-e89b-12d3-a456-426614174000");
    CreateOrderRequest request =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(validProductId, 2)));
    when(uuidGenerator.generate()).thenReturn(orderId);
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    when(uuidGenerator.generate()).thenReturn(orderId);
    mockMvc
        .perform(get("/v1/orders/dddd4567-e89b-12d3-a456-426614174000"))
        .andExpect(status().isOk())
        .andExpect(
            content()
                .json(
                    """
                    {
                      "id": "dddd4567-e89b-12d3-a456-426614174000",
                      "date": "2025-06-25T19:40:28.70219",
                      "items": [
                        {
                          "productId": "222e4567-e89b-12d3-a456-426614174000",
                          "quantity": 2,
                          "unitPrice": {
                            "amount": "10.00",
                            "currency": "INR"
                          }
                        }
                      ],
                      "status": "PENDING",
                      "totalPrice": "20.00"
                    }
                    """));
  }

  @Test
  void shouldReturnNotFoundWhenRetrievingOrderWithInvalidId() throws Exception {
    UUID orderId = UUID.fromString("f1a2b3c4-d5e6-7890-abcd-ef1234567890");
    when(uuidGenerator.generate()).thenReturn(orderId);

    mockMvc
        .perform(get("/v1/orders/f1a2b3c4-d5e6-7890-abcd-ef1234567890"))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath(
                "$.error",
                is("Order does not exists with id: f1a2b3c4-d5e6-7890-abcd-ef1234567890")));
  }

  @Test
  void shouldHandleOptimisticLockingWhenParallelCreateOrderRequestsOccur(CapturedOutput output)
      throws Exception {
    // Arrange: create a product with stock 1
    UUID productId = UUID.fromString("abcd1234-e89b-12d3-a456-426614174000");
    ProductEntity product =
        TestFixture.productEntityBuilder()
            .id(productId)
            .sku("TSH-123-11")
            .version(null)
            .stock(5)
            .build();
    productRepository.save(product);
    CreateOrderRequest orderRequest =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(productId, 1)));
    // Always return a new random UUID for each order
    when(uuidGenerator.generate()).thenAnswer(invocation -> UUID.randomUUID());

    // Use two threads to simulate parallel order creation
    Runnable orderTask =
        () -> {
          try {
            mockMvc
                .perform(
                    post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andReturn();
          } catch (Exception e) {
            // Empty catch block to catch objectMapper exception
          }
        };

    // Start two threads that will try to create an order at the same time
    Thread t1 = new Thread(orderTask);
    Thread t2 = new Thread(orderTask);
    t1.start();
    t2.start();
    t1.join();
    t2.join();

    assertThat(output.getOut()).contains("Data is outdated. Please try again.");

    CreateOrderRequest orderRequestNew =
        new CreateOrderRequest(List.of(new CreateOrderRequest.OrderItemRequest(productId, 4)));
    mockMvc
        .perform(
            post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequestNew)))
        .andExpect(status().isCreated());
  }

  @AfterEach
  void tearDown() {
    orderEntityRepository.deleteAll();
    productRepository.deleteAll();
  }
}
