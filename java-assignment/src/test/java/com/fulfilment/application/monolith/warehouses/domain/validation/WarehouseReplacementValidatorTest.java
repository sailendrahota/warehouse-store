package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseReplacementValidatorTest {

    @Test
    void shouldReturnCurrentWarehouseWhenReplacementIsValid() {

        FakeWarehouseStore store = new FakeWarehouseStore();

        Warehouse current =
                warehouse("MWH.100", "ZWOLLE-001", 50, 30);

        store.warehouses.add(current);

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        Warehouse result = validator.validate(replacement);

        assertNotNull(result);
        assertEquals(current, result);
    }

    @Test
    void shouldRejectNullReplacement() {

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(
                        new FakeWarehouseStore());

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(null));

        assertEquals(
                "Replacement warehouse cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenExistingWarehouseDoesNotExist() {

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(
                        new FakeWarehouseStore());

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(replacement));

        assertEquals(
                "No active warehouse found for business unit code MWH.100",
                exception.getMessage());
    }

    @Test
    void shouldRejectAlreadyArchivedWarehouse() {

        FakeWarehouseStore store = new FakeWarehouseStore();

        Warehouse current =
                warehouse("MWH.100", "ZWOLLE-001", 50, 30);

        current.archivedAt =
                java.time.LocalDateTime.now();

        store.warehouses.add(current);

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(replacement));

        assertEquals(
                "Warehouse MWH.100 is already archived.",
                exception.getMessage());
    }

    @Test
    void shouldRejectNullCapacity() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        replacement.capacity = null;

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse capacity must be non-negative.");
    }

    @Test
    void shouldRejectNegativeCapacity() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", -1, 30);

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse capacity must be non-negative.");
    }

    @Test
    void shouldRejectNullStock() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        replacement.stock = null;

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse stock must be non-negative.");
    }

    @Test
    void shouldRejectNegativeStock() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, -1);

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse stock must be non-negative.");
    }

    @Test
    void shouldRejectCapacityThatCannotAccommodateExistingStock() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 20, 30);

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse capacity cannot accommodate "
                        + "the stock of the existing warehouse.");
    }

    @Test
    void shouldRejectWhenStockDoesNotMatchExistingStock() {

        FakeWarehouseStore store = storeWithCurrentWarehouse();

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 20);

        assertMessage(
                validator,
                replacement,
                "Replacement warehouse stock must match "
                        + "30 the stock of the existing warehouse.");
    }

    private FakeWarehouseStore storeWithCurrentWarehouse() {

        FakeWarehouseStore store = new FakeWarehouseStore();

        store.warehouses.add(
                warehouse("MWH.100", "ZWOLLE-001", 50, 30));

        return store;
    }

    private void assertMessage(
            WarehouseReplacementValidator validator,
            Warehouse replacement,
            String expectedMessage) {

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(replacement));

        assertEquals(
                expectedMessage,
                exception.getMessage());
    }

    private Warehouse warehouse(
            String businessUnitCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = businessUnitCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;

        return warehouse;
    }

    static class FakeWarehouseStore
            implements WarehouseStore {

        List<Warehouse> warehouses =
                new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.remove(warehouse);
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(w ->
                            w.businessUnitCode.equals(buCode))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Warehouse findActiveById(Long id) {
            return warehouses.stream()
                    .filter(w ->
                            w.id != null
                                    && w.id.equals(id)
                                    && w.archivedAt == null)
                    .findFirst()
                    .orElse(null);
        }
    }
}