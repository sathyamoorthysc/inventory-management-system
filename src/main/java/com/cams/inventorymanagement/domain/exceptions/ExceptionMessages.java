package com.cams.inventorymanagement.domain.exceptions;

public final class ExceptionMessages {
  private ExceptionMessages() {}

  public static final String PRODUCT_ID_NULL = "ProductId cannot be null";
  public static final String PRODUCT_ID_VALUE_NULL = "ProductId value cannot be null";
  public static final String PRODUCT_NAME_NULL = "ProductName cannot be null";
  public static final String PRODUCT_NAME_VALUE_NULL_OR_BLANK =
      "ProductName value cannot be null or blank";
  public static final String PRODUCT_SKU_NULL = "ProductSku cannot be null";
  public static final String PRODUCT_SKU_VALUE_NULL_OR_BLANK =
      "ProductSku value cannot be null or blank";
  public static final String PRODUCT_PRICE_NULL = "ProductPrice cannot be null";
  public static final String PRODUCT_PRICE_VALUE_NULL_OR_NEGATIVE =
      "ProductPrice value cannot be null or negative";
  public static final String PRODUCT_PRICE_CURRENCY_NULL = "ProductPrice currency cannot be null";
  public static final String PRODUCT_STOCK_NULL = "ProductStock cannot be null";
  public static final String PRODUCT_STOCK_VALUE_NEGATIVE = "ProductStock value cannot be negative";
  public static final String ORDER_ITEM_ID_NULL = "ItemId value cannot be null";
  public static final String ORDER_QUANTITY_VALUE_ZERO_OR_NEGATIVE =
      "ItemQuantity value cannot be null";
  public static final String ORDER_QUANTITY_VALUE_NULL =
      "ItemQuantity value cannot be zero or negative";
  public static final String ORDER_ID_NULL = "OrderId cannot be null";
  public static final String ORDER_DATE_NULL = "OrderDate cannot be null";
  public static final String ORDER_ITEMS_NULL_OR_EMPTY = "OrderItems cannot be null or empty";
  public static final String ORDER_STATUS_NULL = "OrderStatus cannot be null or empty";
}
