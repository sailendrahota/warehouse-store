package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
class ArchiveWarehouseUseCaseTest {

    @Test
    void shouldArchiveActiveWarehouse() {

        FakeWarehouseStore store =
                new FakeWarehouseStore();

        ArchiveWarehouseUseCase useCase =
                new ArchiveWarehouseUseCase(store);

        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "MWH.100";

        useCase.archive(warehouse);

        assertNotNull(warehouse.archivedAt);
        assertEquals(
                warehouse,
                store.updatedWarehouse);
    }

    static class FakeWarehouseStore
            implements WarehouseStore {

        Warehouse updatedWarehouse;
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
            updatedWarehouse = warehouse;
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