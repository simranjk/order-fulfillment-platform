package com.orderplatform.productservice.controller;

import com.orderplatform.productservice.dto.ProductRequest;
import com.orderplatform.productservice.dto.ProductResponse;
import com.orderplatform.productservice.exception.ProductNotFoundException;
import com.orderplatform.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProductService productService;

  @Test
  void createProduct_shouldReturn201() throws Exception {

    ProductRequest request = new ProductRequest();
    request.setName("Laptop");
    request.setDescription("Business laptop");
    request.setPrice(new BigDecimal("999.99"));
    request.setStockQuantity(10);

    ProductResponse response = new ProductResponse(
      1L,
      "Laptop",
      "Business laptop",
      new BigDecimal("999.99"),
      10,
      true
    );

    when(productService.createProduct(any(ProductRequest.class)))
      .thenReturn(response);

    mockMvc.perform(
        post("/api/v1/products")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isCreated());
  }

  @Test
  void createProduct_withInvalidRequest_shouldReturn400() throws Exception {

    ProductRequest request = new ProductRequest();
    request.setName("");
    request.setPrice(new BigDecimal("-10"));
    request.setStockQuantity(-5);

    mockMvc.perform(
        post("/api/v1/products")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isBadRequest());
  }

  @Test
  void getProduct_shouldReturn200() throws Exception {

    ProductResponse response = new ProductResponse(
      1L,
      "Laptop",
      "Business laptop",
      new BigDecimal("999.99"),
      10,
      true
    );

    when(productService.getProductById(1L))
      .thenReturn(response);

    mockMvc.perform(
        get("/api/v1/products/1")
      )
      .andExpect(status().isOk());
  }

  @Test
  void getProduct_whenNotFound_shouldReturn404() throws Exception {

    when(productService.getProductById(999L))
      .thenThrow(new ProductNotFoundException("Product not found"));

    mockMvc.perform(
        get("/api/v1/products/999")
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void updateProduct_shouldReturn200() throws Exception {

    ProductRequest request = new ProductRequest();
    request.setName("Updated Laptop");
    request.setDescription("Updated business laptop");
    request.setPrice(new BigDecimal("1099.99"));
    request.setStockQuantity(15);

    ProductResponse response = new ProductResponse(
      1L,
      "Updated Laptop",
      "Updated business laptop",
      new BigDecimal("1099.99"),
      15,
      true
    );

    when(productService.updateProduct(any(Long.class), any(ProductRequest.class)))
      .thenReturn(response);

    mockMvc.perform(
        put("/api/v1/products/1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isOk());
  }

  @Test
  void updateProduct_whenNotFound_shouldReturn404() throws Exception {

    ProductRequest request = new ProductRequest();
    request.setName("Laptop");
    request.setPrice(new BigDecimal("999.99"));
    request.setStockQuantity(10);

    when(productService.updateProduct(any(Long.class), any(ProductRequest.class)))
      .thenThrow(new ProductNotFoundException("Product not found"));

    mockMvc.perform(
        put("/api/v1/products/999")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request))
      )
      .andExpect(status().isNotFound());
  }

  @Test
  void deleteProduct_shouldReturn204() throws Exception {

    mockMvc.perform(
        delete("/api/v1/products/1")
      )
      .andExpect(status().isNoContent());
  }

  @Test
  void deleteProduct_whenNotFound_shouldReturn404() throws Exception {

    doThrow(new ProductNotFoundException("Product not found"))
      .when(productService)
      .deleteProduct(999L);

    mockMvc.perform(
        delete("/api/v1/products/999")
      )
      .andExpect(status().isNotFound());
  }

}