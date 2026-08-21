package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseArchiveValidatorTest {

    @Test
    void shouldAcceptActiveWarehouse() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";

        assertDoesNotThrow(
                () -> WarehouseArchiveValidator.validate(warehouse));
    }

    @Test
    void shouldRejectNullWarehouse() {

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> WarehouseArchiveValidator.validate(null));

        assertEquals(
                "Warehouse cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";
        warehouse.archivedAt = LocalDateTime.now();

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> WarehouseArchiveValidator.validate(warehouse));

        assertEquals(
                "Warehouse MWH.100 is already archived.",
                exception.getMessage());
    }
}