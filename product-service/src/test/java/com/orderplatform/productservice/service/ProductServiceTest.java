package com.orderplatform.productservice.service;

import com.orderplatform.productservice.dto.ProductRequest;
import com.orderplatform.productservice.dto.ProductResponse;
import com.orderplatform.productservice.entity.Product;
import com.orderplatform.productservice.exception.ProductNotFoundException;
import com.orderplatform.productservice.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

  @Mock
  private ProductRepository productRepository;

  @InjectMocks
  private ProductService productService;

  @Test
  void createProduct_shouldReturnProduct() {

    ProductRequest request = new ProductRequest();
    request.setName("Laptop");
    request.setDescription("Business laptop");
    request.setPrice(new BigDecimal("999.99"));
    request.setStockQuantity(10);

    Product savedProduct = new Product();
    savedProduct.setName("Laptop");
    savedProduct.setDescription("Business laptop");
    savedProduct.setPrice(new BigDecimal("999.99"));
    savedProduct.setStockQuantity(10);

    when(productRepository.save(any(Product.class)))
      .thenReturn(savedProduct);

    ProductResponse response = productService.createProduct(request);

    assertEquals("Laptop", response.getName());
    assertEquals(new BigDecimal("999.99"), response.getPrice());
    assertEquals(10, response.getStockQuantity());
  }

  @Test
  void getProductById_shouldReturnProduct() {

    Product product = new Product();
    product.setName("Laptop");
    product.setDescription("Business laptop");
    product.setPrice(new BigDecimal("999.99"));
    product.setStockQuantity(10);

    when(productRepository.findById(1L))
      .thenReturn(Optional.of(product));

    ProductResponse response = productService.getProductById(1L);

    assertEquals("Laptop", response.getName());
    assertEquals(new BigDecimal("999.99"), response.getPrice());
    assertEquals(10, response.getStockQuantity());
  }

  @Test
  void getProductById_whenNotFound_shouldThrowException() {

    when(productRepository.findById(999L))
      .thenReturn(Optional.empty());

    assertThrows(
      ProductNotFoundException.class,
      () -> productService.getProductById(999L)
    );
  }

  @Test
  void getAllProducts_shouldReturnProducts() {

    Product product1 = new Product();
    product1.setName("Laptop");
    product1.setPrice(new BigDecimal("999.99"));
    product1.setStockQuantity(10);

    Product product2 = new Product();
    product2.setName("Phone");
    product2.setPrice(new BigDecimal("599.99"));
    product2.setStockQuantity(20);

    when(productRepository.findAll())
      .thenReturn(List.of(product1, product2));

    List<ProductResponse> response = productService.getAllProducts();

    assertEquals(2, response.size());
    assertEquals("Laptop", response.get(0).getName());
    assertEquals("Phone", response.get(1).getName());
  }

  @Test
  void updateProduct_shouldReturnUpdatedProduct() {

    ProductRequest request = new ProductRequest();
    request.setName("Updated Laptop");
    request.setDescription("Updated laptop");
    request.setPrice(new BigDecimal("1099.99"));
    request.setStockQuantity(15);

    Product existingProduct = new Product();
    existingProduct.setName("Laptop");
    existingProduct.setPrice(new BigDecimal("999.99"));
    existingProduct.setStockQuantity(10);

    when(productRepository.findById(1L))
      .thenReturn(Optional.of(existingProduct));

    when(productRepository.save(any(Product.class)))
      .thenReturn(existingProduct);

    ProductResponse response = productService.updateProduct(1L, request);

    assertEquals("Updated Laptop", response.getName());
    assertEquals(new BigDecimal("1099.99"), response.getPrice());
    assertEquals(15, response.getStockQuantity());
  }

  @Test
  void deleteProduct_shouldDeleteProduct() {

    Product product = new Product();

    when(productRepository.findById(1L))
      .thenReturn(Optional.of(product));

    productService.deleteProduct(1L);

    verify(productRepository).delete(product);
  }

  @Test
  void deleteProduct_whenNotFound_shouldThrowException() {

    when(productRepository.findById(999L))
      .thenReturn(Optional.empty());

    assertThrows(
      ProductNotFoundException.class,
      () -> productService.deleteProduct(999L)
    );

    verify(productRepository, never()).delete(any(Product.class));
  }
}