
package com.orderplatform.productservice.service;

import com.orderplatform.productservice.dto.ProductRequest;
import com.orderplatform.productservice.dto.ProductResponse;
import com.orderplatform.productservice.entity.Product;
import com.orderplatform.productservice.exception.ProductNotFoundException;
import com.orderplatform.productservice.mapper.ProductMapper;
import com.orderplatform.productservice.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public ProductResponse createProduct(ProductRequest request) {

    Product product = ProductMapper.toEntity(request);

    Product savedProduct = productRepository.save(product);

    return ProductMapper.toResponse(savedProduct);
  }

  public ProductResponse getProductById(Long id) {

    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ProductNotFoundException("Product not found"));

    return ProductMapper.toResponse(product);
  }

  public List<ProductResponse> getAllProducts() {

    return productRepository.findAll()
      .stream()
      .map(ProductMapper::toResponse)
      .toList();
  }

  public ProductResponse updateProduct(Long id, ProductRequest request) {

    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ProductNotFoundException("Product not found"));

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setPrice(request.getPrice());
    product.setStockQuantity(request.getStockQuantity());

    Product updatedProduct = productRepository.save(product);

    return ProductMapper.toResponse(updatedProduct);
  }

  public void deleteProduct(Long id) {

    Product product = productRepository.findById(id)
      .orElseThrow(() -> new ProductNotFoundException("Product not found"));

    productRepository.delete(product);
  }
}
