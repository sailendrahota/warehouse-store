package com.fulfilment.application.monolith.warehouses.domain.usecases;

import java.time.LocalDateTime;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

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
      throw new  WebApplicationException("Warehouse cannot be null.",400);
    }

    if (warehouse.archivedAt != null) {
      throw new  WebApplicationException(
          "Warehouse " + warehouse.businessUnitCode + " is already archived.",404);
    }

    warehouse.archivedAt = LocalDateTime.now();

    warehouseStore.update(warehouse);
  }

}
