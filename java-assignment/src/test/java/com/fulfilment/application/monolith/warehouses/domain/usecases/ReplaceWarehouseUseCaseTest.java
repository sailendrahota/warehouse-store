package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseReplacementValidator;

import java.util.ArrayList;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReplaceWarehouseUseCaseTest {

    @Test
    void shouldArchiveOldWarehouseAndCreateReplacement() {

        FakeWarehouseStore store = new FakeWarehouseStore();

        Warehouse oldWarehouse =
                warehouse("MWH.100", "ZWOLLE-001", 50, 30);

        store.warehouses.add(oldWarehouse);

        WarehouseReplacementValidator validator =
                new WarehouseReplacementValidator(store);

        ReplaceWarehouseUseCase useCase =
                new ReplaceWarehouseUseCase(
                        store,
                        validator);

        Warehouse replacement =
                warehouse("MWH.100", "ZWOLLE-001", 40, 30);

        useCase.replace(replacement);

        assertNotNull(oldWarehouse.archivedAt);
        assertEquals(oldWarehouse, store.updatedWarehouse);
        assertEquals(replacement, store.createdWarehouse);
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

    static class FakeWarehouseStore implements WarehouseStore {

        List<Warehouse> warehouses = new ArrayList<>();

        Warehouse updatedWarehouse;
        Warehouse createdWarehouse;

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            createdWarehouse = warehouse;
            warehouses.add(warehouse);
        }

        @Override
        public void update(Warehouse warehouse) {
            updatedWarehouse = warehouse;
        }

        @Override
        public void remove(Warehouse warehouse) {
            warehouses.remove(warehouse);
        }

        @Override
        public Warehouse findByBusinessUnitCode(String buCode) {
            return warehouses.stream()
                    .filter(w -> w.businessUnitCode.equals(buCode))
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