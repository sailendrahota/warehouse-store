package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WarehouseReplacementValidator {

    private final WarehouseStore warehouseStore;

    public WarehouseReplacementValidator(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    public Warehouse validate(Warehouse newWarehouse) {

        if (newWarehouse == null) {
            throw new WarehouseValidationException(
                    "Replacement warehouse cannot be null.");
        }

        Warehouse currentWarehouse =
                warehouseStore.findByBusinessUnitCode(
                        newWarehouse.businessUnitCode);

        if (currentWarehouse == null) {
            throw new WarehouseValidationException(
                    "No active warehouse found for business unit code "
                            + newWarehouse.businessUnitCode);
        }

        if (currentWarehouse.archivedAt != null) {
            throw new WarehouseValidationException(
                    "Warehouse " + newWarehouse.businessUnitCode
                            + " is already archived.");
        }

        if (newWarehouse.capacity == null || newWarehouse.capacity < 0) {
            throw new WarehouseValidationException(
                    "Replacement warehouse capacity must be non-negative.");
        }

        if (newWarehouse.stock == null || newWarehouse.stock < 0) {
            throw new WarehouseValidationException(
                    "Replacement warehouse stock must be non-negative.");
        }

        if (newWarehouse.capacity < currentWarehouse.stock) {
            throw new WarehouseValidationException(
                    "Replacement warehouse capacity cannot accommodate "
                            + "the stock of the existing warehouse.");
        }

        if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
            throw new WarehouseValidationException(
                    "Replacement warehouse stock must match "
                            + currentWarehouse.stock
                            + " the stock of the existing warehouse.");
        }



        return currentWarehouse;
    }
}