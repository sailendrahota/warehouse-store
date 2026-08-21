package com.fulfilment.application.monolith.fulfillment.domain.model;

public class Fulfillment {

    public Long id;
    public Long storeId;
    public Long productId;
    public Long warehouseId;

    public Fulfillment(Long productId, Long storeId, Long warehouseId) {
        this.productId = productId;
        this.storeId = storeId;
        this.warehouseId = warehouseId;
    }
    public Fulfillment() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId) {
        this.warehouseId = warehouseId;
    }


}
