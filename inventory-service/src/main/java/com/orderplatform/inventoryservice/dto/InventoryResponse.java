package com.orderplatform.inventoryservice.dto;

public class InventoryResponse {

    private Long productId;
    private Integer availableQuantity;
    private Integer reservedQuantity;

    public InventoryResponse() {
    }

    public InventoryResponse(
            Long productId,
            Integer availableQuantity,
            Integer reservedQuantity) {

        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
}
