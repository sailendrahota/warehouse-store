package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseCreationValidator;
import java.util.ArrayList;
import java.util.List;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
@QuarkusTest
class CreateWarehouseUseCaseTest {

    @Test
    void shouldCreateWarehouseWhenValidationPasses() {

        FakeWarehouseStore warehouseStore =
                new FakeWarehouseStore();

        WarehouseCreationValidator validator = new WarehouseCreationValidator(warehouseStore,
                        identifier -> new Location(
                                "ZWOLLE-001",
                                2,
                                100));
        CreateWarehouseUseCase useCase =
                new CreateWarehouseUseCase(
                        warehouseStore,
                        validator);

        Warehouse warehouse =
                warehouse("MWH.100", "ZWOLLE-001", 40, 20);

        useCase.create(warehouse);

        assertEquals(
                1,
                warehouseStore.createdWarehouses.size());

        assertEquals(
                "MWH.100",
                warehouseStore.createdWarehouses
                        .get(0)
                        .businessUnitCode);
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

        List<Warehouse> warehouses = new ArrayList<>();
        List<Warehouse> createdWarehouses = new ArrayList<>();

        @Override
        public List<Warehouse> getAll() {
            return warehouses;
        }

        @Override
        public void create(Warehouse warehouse) {
            createdWarehouses.add(warehouse);
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