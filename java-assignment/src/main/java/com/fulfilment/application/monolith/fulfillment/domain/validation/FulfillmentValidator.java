package com.fulfilment.application.monolith.fulfillment.domain.validation;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FulfillmentValidator {

    private final FulfillmentStore fulfillmentStore;

    public FulfillmentValidator(FulfillmentStore fulfillmentStore) {
        this.fulfillmentStore = fulfillmentStore;
    }

    public void validate(Fulfillment fulfillment) {

        if (fulfillment == null) {
            throw new FulfillmentValidationException(
                    "Fulfillment request cannot be null.");
        }

        if (fulfillment.storeId == null) {
            throw new FulfillmentValidationException(
                    "Store id is required.");
        }

        if (fulfillment.productId == null) {
            throw new FulfillmentValidationException(
                    "Product id is required.");
        }

        if (fulfillment.warehouseId == null) {
            throw new FulfillmentValidationException(
                    "Warehouse id is required.");
        }

        if (!fulfillmentStore.storeExists(fulfillment.storeId)) {
            throw new FulfillmentValidationException(
                    "Store with id " + fulfillment.storeId + " does not exist.");
        }

        if (!fulfillmentStore.productExists(fulfillment.productId)) {
            throw new FulfillmentValidationException(
                    "Product with id " + fulfillment.productId + " does not exist.");
        }

        if (!fulfillmentStore.warehouseExists(fulfillment.warehouseId)) {
            throw new FulfillmentValidationException(
                    "Active warehouse with id "
                            + fulfillment.warehouseId
                            + " does not exist.");
        }

        if (fulfillmentStore.exists(
                fulfillment.storeId,
                fulfillment.productId,
                fulfillment.warehouseId)) {

            throw new FulfillmentValidationException(
                    "Fulfillment association already exists.");
        }

        if (fulfillmentStore.countWarehousesForProductAndStore(
                fulfillment.storeId,
                fulfillment.productId) >= 2) {

            throw new FulfillmentValidationException(
                    "A product can be fulfilled by a maximum of 2 warehouses per store.");
        }

        if (fulfillmentStore.countWarehousesForStore(
                fulfillment.storeId) >= 3) {

            throw new FulfillmentValidationException(
                    "A store can be fulfilled by a maximum of 3 warehouses.");
        }

        if (fulfillmentStore.countProductsForWarehouse(
                fulfillment.warehouseId) >= 5) {

            throw new FulfillmentValidationException(
                    "A warehouse can store a maximum of 5 product types.");
        }
    }
}