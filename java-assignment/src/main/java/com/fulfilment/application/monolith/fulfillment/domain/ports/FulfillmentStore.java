package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;

import java.util.List;

public interface FulfillmentStore {

    boolean productExists(Long productId);

    boolean storeExists(Long storeId);

    boolean warehouseExists(Long warehouseId);

    boolean exists(Long storeId, Long productId, Long warehouseId);

    long countWarehousesForProductAndStore(Long storeId, Long productId);

    long countWarehousesForStore(Long storeId);

    long countProductsForWarehouse(Long warehouseId);

    void create(Fulfillment fulfillment);

    List<Fulfillment> getAll();
}