package com.orderplatform.inventoryservice.controller;

import com.orderplatform.inventoryservice.controller.InventoryController;
import com.orderplatform.inventoryservice.dto.InventoryResponse;
import com.orderplatform.inventoryservice.exception.GlobalExceptionHandler;
import com.orderplatform.inventoryservice.exception.InsufficientInventoryException;
import com.orderplatform.inventoryservice.exception.InventoryNotFoundException;
import com.orderplatform.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryService inventoryService;

    @Test
    void shouldCreateInventory() throws Exception {

        InventoryResponse response =
                new InventoryResponse("PROD-1001", 100, 0);

        when(inventoryService.createInventory(any()))
                .thenReturn(response);

        String request = """
                {
                    "productId": "PROD-1001",
                    "quantity": 100
                }
                """;

        mockMvc.perform(post("/api/v1/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId")
                        .value("PROD-1001"))
                .andExpect(jsonPath("$.availableQuantity")
                        .value(100))
                .andExpect(jsonPath("$.reservedQuantity")
                        .value(0));
    }

    @Test
    void shouldGetInventory() throws Exception {

        InventoryResponse response =
                new InventoryResponse("PROD-1001", 95, 5);

        when(inventoryService.getInventory("PROD-1001"))
                .thenReturn(response);

        mockMvc.perform(
                get("/api/v1/inventory/PROD-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId")
                        .value("PROD-1001"))
                .andExpect(jsonPath("$.availableQuantity")
                        .value(95))
                .andExpect(jsonPath("$.reservedQuantity")
                        .value(5));
    }

    @Test
    void shouldReserveInventory() throws Exception {

        InventoryResponse response =
                new InventoryResponse("PROD-1001", 95, 5);

        when(inventoryService.reserveInventory(
                eq("PROD-1001"), any()))
                .thenReturn(response);

        String request = """
                {
                    "quantity": 5
                }
                """;

        mockMvc.perform(
                post("/api/v1/inventory/PROD-1001/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity")
                        .value(95))
                .andExpect(jsonPath("$.reservedQuantity")
                        .value(5));
    }

    @Test
    void shouldReleaseInventory() throws Exception {

        InventoryResponse response =
                new InventoryResponse("PROD-1001", 100, 0);

        when(inventoryService.releaseInventory(
                eq("PROD-1001"), any()))
                .thenReturn(response);

        String request = """
                {
                    "quantity": 5
                }
                """;

        mockMvc.perform(
                post("/api/v1/inventory/PROD-1001/release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availableQuantity")
                        .value(100))
                .andExpect(jsonPath("$.reservedQuantity")
                        .value(0));
    }

    @Test
    void shouldReturnNotFoundWhenInventoryDoesNotExist()
            throws Exception {

        when(inventoryService.getInventory("PROD-9999"))
                .thenThrow(new InventoryNotFoundException(
                        "Inventory not found for product: PROD-9999"));

        mockMvc.perform(
                get("/api/v1/inventory/PROD-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("INVENTORY_NOT_FOUND"));
    }

    @Test
    void shouldReturnConflictWhenInventoryIsInsufficient()
            throws Exception {

        when(inventoryService.reserveInventory(
                eq("PROD-1001"), any()))
                .thenThrow(new InsufficientInventoryException(
                        "Insufficient inventory for product: PROD-1001"));

        String request = """
                {
                    "quantity": 101
                }
                """;

        mockMvc.perform(
                post("/api/v1/inventory/PROD-1001/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error")
                        .value("INSUFFICIENT_INVENTORY"));
    }

    @Test
    void shouldRejectInvalidQuantity() throws Exception {

        String request = """
                {
                    "quantity": 0
                }
                """;

        mockMvc.perform(
                post("/api/v1/inventory/PROD-1001/reserve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("VALIDATION_ERROR"));
    }
}
