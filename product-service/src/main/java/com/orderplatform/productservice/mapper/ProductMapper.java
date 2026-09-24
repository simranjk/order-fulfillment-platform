package com.orderplatform.productservice.mapper;

import com.orderplatform.productservice.dto.ProductRequest;
import com.orderplatform.productservice.dto.ProductResponse;
import com.orderplatform.productservice.entity.Product;

public class ProductMapper {

  public static Product toEntity(ProductRequest request) {

    Product product = new Product();

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    return product;
  }

  public static ProductResponse toResponse(Product product) {

    return new ProductResponse(
      product.getId(),
      product.getName(),
      product.getDescription(),
      product.getPrice(),
      product.getStockQuantity(),
      product.getActive()
    );
  }
}