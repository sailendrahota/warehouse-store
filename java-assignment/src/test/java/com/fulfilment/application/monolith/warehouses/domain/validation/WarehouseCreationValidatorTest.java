package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarehouseCreationValidatorTest {

    @Test
    void shouldAcceptValidWarehouse() {

        WarehouseStore store = new FakeWarehouseStore();

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        store,
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        validator.validate(
                warehouse("MWH.100", "ZWOLLE-001", 40, 20));
    }

    @Test
    void shouldRejectNullWarehouse() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(null));

        assertEquals(
                "Warehouse cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateActiveBusinessUnitCode() {

        FakeWarehouseStore store =
                new FakeWarehouseStore();

        store.warehouses.add(
                warehouse("MWH.100", "ZWOLLE-001", 40, 20));

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        store,
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(
                                warehouse(
                                        "MWH.100",
                                        "ZWOLLE-001",
                                        30,
                                        10)));

        assertEquals(
                "Warehouse with business unit code MWH.100 already exists.",
                exception.getMessage());
    }

    @Test
    void shouldAllowSameBusinessUnitCodeWhenPreviousWarehouseIsArchived() {

        FakeWarehouseStore store =
                new FakeWarehouseStore();

        Warehouse archived =
                warehouse(
                        "MWH.100",
                        "ZWOLLE-001",
                        40,
                        20);

        archived.archivedAt =
                java.time.LocalDateTime.now();

        store.warehouses.add(archived);

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        store,
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        validator.validate(
                warehouse(
                        "MWH.100",
                        "ZWOLLE-001",
                        40,
                        20));
    }

    @Test
    void shouldRejectInvalidLocation() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(null));

        WarehouseValidationException exception =
                assertThrows(
                        WarehouseValidationException.class,
                        () -> validator.validate(
                                warehouse(
                                        "MWH.100",
                                        "UNKNOWN",
                                        30,
                                        10)));

        assertEquals(
                "Location UNKNOWN does not exist.",
                exception.getMessage());
    }

    @Test
    void shouldRejectWhenMaximumNumberOfWarehousesReached() {

        FakeWarehouseStore store =
                new FakeWarehouseStore();

        store.warehouses.add(
                warehouse("MWH.001", "ZWOLLE-001", 20, 10));

        store.warehouses.add(
                warehouse("MWH.002", "ZWOLLE-001", 20, 10));

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        store,
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.003",
                                "ZWOLLE-001",
                                20,
                                10)));
    }

    @Test
    void shouldRejectNegativeCapacity() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.100",
                                "ZWOLLE-001",
                                -1,
                                0)));
    }

    @Test
    void shouldRejectCapacityAboveLocationCapacity() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        50)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.100",
                                "ZWOLLE-001",
                                60,
                                20)));
    }

    @Test
    void shouldRejectNegativeStock() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.100",
                                "ZWOLLE-001",
                                40,
                                -1)));
    }

    @Test
    void shouldRejectStockAboveCapacity() {

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        new FakeWarehouseStore(),
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        2,
                                        100)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.100",
                                "ZWOLLE-001",
                                40,
                                50)));
    }

    @Test
    void shouldRejectWhenTotalLocationCapacityIsExceeded() {

        FakeWarehouseStore store =
                new FakeWarehouseStore();

        store.warehouses.add(
                warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        70,
                        20));

        WarehouseCreationValidator validator =
                new WarehouseCreationValidator(
                        store,
                        new FakeLocationResolver(
                                new Location(
                                        "ZWOLLE-001",
                                        3,
                                        100)));

        assertThrows(
                WarehouseValidationException.class,
                () -> validator.validate(
                        warehouse(
                                "MWH.002",
                                "ZWOLLE-001",
                                40,
                                20)));
    }

    private Warehouse warehouse(
            String buCode,
            String location,
            int capacity,
            int stock) {

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = buCode;
        warehouse.location = location;
        warehouse.capacity = capacity;
        warehouse.stock = stock;
        return warehouse;
    }

    static class FakeLocationResolver
            implements LocationResolver {

        private final Location location;

        FakeLocationResolver(Location location) {
            this.location = location;
        }

        @Override
        public Location resolveByIdentifier(String identifier) {
            return location;
        }
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