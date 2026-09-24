package com.orderplatform.productservice.dto;

import java.math.BigDecimal;

public class ProductResponse {

  private Long id;
  private String name;
  private String description;
  private BigDecimal price;
  private Integer stockQuantity;
  private Boolean active;

  public ProductResponse() {
  }

  public ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    Integer stockQuantity,
    Boolean active) {

    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.stockQuantity = stockQuantity;
    this.active = active;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getStockQuantity() {
    return stockQuantity;
  }

  public Boolean getActive() {
    return active;
  }
}