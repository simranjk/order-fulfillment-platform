package com.orderplatform.inventoryservice.service;

import com.orderplatform.inventoryservice.dto.InventoryCreateRequest;
import com.orderplatform.inventoryservice.dto.InventoryQuantityRequest;
import com.orderplatform.inventoryservice.dto.InventoryResponse;
import com.orderplatform.inventoryservice.entity.Inventory;
import com.orderplatform.inventoryservice.exception.InsufficientInventoryException;
import com.orderplatform.inventoryservice.exception.InventoryNotFoundException;
import com.orderplatform.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        inventory.setProductId("PROD-1001");
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(0);
    }

    @Test
    void shouldCreateInventory() {

        InventoryCreateRequest request = new InventoryCreateRequest();
        request.setProductId("PROD-1001");
        request.setQuantity(100);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.createInventory(request);

        assertEquals("PROD-1001", response.getProductId());
        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldGetInventory() {

        when(inventoryRepository.findByProductId("PROD-1001"))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response =
                inventoryService.getInventory("PROD-1001");

        assertEquals("PROD-1001", response.getProductId());
        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());
    }

    @Test
    void shouldThrowExceptionWhenInventoryNotFound() {

        when(inventoryRepository.findByProductId("PROD-9999"))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventory("PROD-9999")
        );
    }

    @Test
    void shouldReserveInventory() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(5);

        when(inventoryRepository.findByProductId("PROD-1001"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.reserveInventory(
                        "PROD-1001",
                        request);

        assertEquals(95, response.getAvailableQuantity());
        assertEquals(5, response.getReservedQuantity());

        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldRejectReservationWhenInventoryIsInsufficient() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(101);

        when(inventoryRepository.findByProductId("PROD-1001"))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InsufficientInventoryException.class,
                () -> inventoryService.reserveInventory(
                        "PROD-1001",
                        request)
        );

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldReleaseInventory() {

        inventory.setAvailableQuantity(95);
        inventory.setReservedQuantity(5);

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(5);

        when(inventoryRepository.findByProductId("PROD-1001"))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.releaseInventory(
                        "PROD-1001",
                        request);

        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());

        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldRejectReleaseWhenQuantityExceedsReserved() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(5);

        when(inventoryRepository.findByProductId("PROD-1001"))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.releaseInventory(
                        "PROD-1001",
                        request)
        );

        verify(inventoryRepository, never()).save(any());
    }
}
