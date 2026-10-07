package com.orderplatform.inventoryservice.service;

import com.orderplatform.inventoryservice.dto.InventoryCreateRequest;
import com.orderplatform.inventoryservice.dto.InventoryQuantityRequest;
import com.orderplatform.inventoryservice.dto.InventoryResponse;
import com.orderplatform.inventoryservice.entity.Inventory;
import com.orderplatform.inventoryservice.exception.InsufficientInventoryException;
import com.orderplatform.inventoryservice.exception.InventoryNotFoundException;
import com.orderplatform.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse createInventory(InventoryCreateRequest request) {

        Inventory inventory = new Inventory();

        inventory.setProductId(request.getProductId());
        inventory.setAvailableQuantity(request.getQuantity());
        inventory.setReservedQuantity(0);

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    public InventoryResponse getInventory(Long productId) {

        Inventory inventory = findByProductId(productId);

        return toResponse(inventory);
    }

    public InventoryResponse reserveInventory(
            Long productId,
            InventoryQuantityRequest request) {

        Inventory inventory = findByProductId(productId);

        if (inventory.getAvailableQuantity() < request.getQuantity()) {
            throw new InsufficientInventoryException(
                    "Insufficient inventory for product: " + productId);
        }

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() - request.getQuantity());

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() + request.getQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    public InventoryResponse releaseInventory(
            Long productId,
            InventoryQuantityRequest request) {

        Inventory inventory = findByProductId(productId);

        if (inventory.getReservedQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException(
                    "Cannot release more inventory than reserved");
        }

        inventory.setReservedQuantity(
                inventory.getReservedQuantity() - request.getQuantity());

        inventory.setAvailableQuantity(
                inventory.getAvailableQuantity() + request.getQuantity());

        Inventory savedInventory = inventoryRepository.save(inventory);

        return toResponse(savedInventory);
    }

    private Inventory findByProductId(Long productId) {

        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product: " + productId));
    }

    private InventoryResponse toResponse(Inventory inventory) {

        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity());
    }
}
