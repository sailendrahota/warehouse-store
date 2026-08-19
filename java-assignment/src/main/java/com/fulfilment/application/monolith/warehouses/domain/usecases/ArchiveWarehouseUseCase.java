package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }
  @Override
  @Transactional
  public void archive(Warehouse warehouse) {

    if (warehouse == null) {
      throw new IllegalArgumentException("Warehouse cannot be null.");
    }

    if (warehouse.archivedAt != null) {
      throw new IllegalArgumentException(
          "Warehouse " + warehouse.businessUnitCode + " is already archived.");
    }

    warehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(warehouse);
  }

}
