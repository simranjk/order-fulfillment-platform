package com.orderplatform.inventoryservice.controller;

import com.orderplatform.inventoryservice.dto.InventoryCreateRequest;
import com.orderplatform.inventoryservice.dto.InventoryQuantityRequest;
import com.orderplatform.inventoryservice.dto.InventoryResponse;
import com.orderplatform.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InventoryResponse createInventory(
            @Valid @RequestBody InventoryCreateRequest request) {

        return inventoryService.createInventory(request);
    }

    @GetMapping("/{productId}")
    public InventoryResponse getInventory(
            @PathVariable String productId) {

        return inventoryService.getInventory(productId);
    }

    @PostMapping("/{productId}/reserve")
    public InventoryResponse reserveInventory(
            @PathVariable String productId,
            @Valid @RequestBody InventoryQuantityRequest request) {

        return inventoryService.reserveInventory(productId, request);
    }

    @PostMapping("/{productId}/release")
    public InventoryResponse releaseInventory(
            @PathVariable String productId,
            @Valid @RequestBody InventoryQuantityRequest request) {

        return inventoryService.releaseInventory(productId, request);
    }
}
