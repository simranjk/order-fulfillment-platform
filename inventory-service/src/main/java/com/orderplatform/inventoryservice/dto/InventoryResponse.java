package com.orderplatform.inventoryservice.dto;

public class InventoryResponse {

    private String productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;

    public InventoryResponse(
            String productId,
            Integer availableQuantity,
            Integer reservedQuantity) {

        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
}
