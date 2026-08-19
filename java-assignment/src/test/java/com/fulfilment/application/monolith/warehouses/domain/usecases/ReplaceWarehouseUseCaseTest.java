package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {

  @Test
  void shouldArchiveOldWarehouseAndCreateReplacement() {

    FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

    Warehouse oldWarehouse =
        warehouse("MWH.100", "ZWOLLE-001", 50, 30);

    warehouseStore.warehouses.add(oldWarehouse);

    ReplaceWarehouseUseCase useCase =
        new ReplaceWarehouseUseCase(warehouseStore);

    Warehouse replacement =
        warehouse("MWH.100", "ZWOLLE-001", 40, 30);

    useCase.replace(replacement);

    assertNotNull(oldWarehouse.archivedAt);

    assertEquals(1, warehouseStore.createdWarehouses.size());

    assertEquals(
        "MWH.100",
        warehouseStore.createdWarehouses.get(0).businessUnitCode);

    assertEquals(
        30,
        warehouseStore.createdWarehouses.get(0).stock);
  }

  @Test
  void shouldRejectReplacementWhenOldWarehouseDoesNotExist() {

    FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

    ReplaceWarehouseUseCase useCase =
        new ReplaceWarehouseUseCase(warehouseStore);

    Warehouse replacement =
        warehouse("MWH.100", "ZWOLLE-001", 40, 30);

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.replace(replacement));
  }

  @Test
  void shouldRejectWhenCapacityCannotAccommodateExistingStock() {

    FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

    warehouseStore.warehouses.add(
        warehouse("MWH.100", "ZWOLLE-001", 50, 30));

    ReplaceWarehouseUseCase useCase =
        new ReplaceWarehouseUseCase(warehouseStore);

    Warehouse replacement =
        warehouse("MWH.100", "ZWOLLE-001", 20, 30);

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.replace(replacement));
  }

  @Test
  void shouldRejectWhenStockDoesNotMatchExistingStock() {

    FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

    warehouseStore.warehouses.add(
        warehouse("MWH.100", "ZWOLLE-001", 50, 30));

    ReplaceWarehouseUseCase useCase =
        new ReplaceWarehouseUseCase(warehouseStore);

    Warehouse replacement =
        warehouse("MWH.100", "ZWOLLE-001", 40, 20);

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.replace(replacement));
  }

  @Test
  void shouldRejectAlreadyArchivedWarehouse() {

    FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

    Warehouse oldWarehouse =
        warehouse("MWH.100", "ZWOLLE-001", 50, 30);

    oldWarehouse.archivedAt =
        java.time.LocalDateTime.now();

    warehouseStore.warehouses.add(oldWarehouse);

    ReplaceWarehouseUseCase useCase =
        new ReplaceWarehouseUseCase(warehouseStore);

    Warehouse replacement =
        warehouse("MWH.100", "ZWOLLE-001", 40, 30);

    assertThrows(
        IllegalArgumentException.class,
        () -> useCase.replace(replacement));
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
      // The fake object is already in the list, so its
      // archivedAt change is automatically visible.
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
  }
}