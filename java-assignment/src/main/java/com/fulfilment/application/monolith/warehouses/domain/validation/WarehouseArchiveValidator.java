package com.fulfilment.application.monolith.warehouses.domain.validation;


import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public final  class WarehouseArchiveValidator {

    public static void validate(Warehouse warehouse) {

        if (warehouse == null) {
            throw new WarehouseValidationException(
                    "Warehouse cannot be null.");
        }

        if (warehouse.archivedAt != null) {
            throw new WarehouseValidationException(
                    "Warehouse " + warehouse.businessUnitCode
                            + " is already archived.");
        }
    }
}
