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
        inventory.setProductId(1001L);
        inventory.setAvailableQuantity(100);
        inventory.setReservedQuantity(0);
    }

    @Test
    void shouldCreateInventory() {

        InventoryCreateRequest request = new InventoryCreateRequest();
        request.setProductId(1001L);
        request.setQuantity(100);

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.createInventory(request);

        assertEquals(1001L, response.getProductId());
        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void shouldGetInventory() {

        when(inventoryRepository.findByProductId(1001L))
                .thenReturn(Optional.of(inventory));

        InventoryResponse response =
                inventoryService.getInventory(1001L);

        assertEquals(1001L, response.getProductId());
        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());
    }

    @Test
    void shouldThrowExceptionWhenInventoryNotFound() {

        when(inventoryRepository.findByProductId(9999L))
                .thenReturn(Optional.empty());

        assertThrows(
                InventoryNotFoundException.class,
                () -> inventoryService.getInventory(9999L)
        );
    }

    @Test
    void shouldReserveInventory() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(5);

        when(inventoryRepository.findByProductId(1001L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.reserveInventory(
                        1001L,
                        request);

        assertEquals(95, response.getAvailableQuantity());
        assertEquals(5, response.getReservedQuantity());

        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldRejectReservationWhenInventoryIsInsufficient() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(101);

        when(inventoryRepository.findByProductId(1001L))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                InsufficientInventoryException.class,
                () -> inventoryService.reserveInventory(
                        1001L,
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

        when(inventoryRepository.findByProductId(1001L))
                .thenReturn(Optional.of(inventory));

        when(inventoryRepository.save(any(Inventory.class)))
                .thenReturn(inventory);

        InventoryResponse response =
                inventoryService.releaseInventory(
                        1001L,
                        request);

        assertEquals(100, response.getAvailableQuantity());
        assertEquals(0, response.getReservedQuantity());

        verify(inventoryRepository).save(inventory);
    }

    @Test
    void shouldRejectReleaseWhenQuantityExceedsReserved() {

        InventoryQuantityRequest request = new InventoryQuantityRequest();
        request.setQuantity(5);

        when(inventoryRepository.findByProductId(1001L))
                .thenReturn(Optional.of(inventory));

        assertThrows(
                IllegalArgumentException.class,
                () -> inventoryService.releaseInventory(
                        1001L,
                        request)
        );

        verify(inventoryRepository, never()).save(any());
    }
}
