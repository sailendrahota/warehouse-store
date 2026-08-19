package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

	private final WarehouseStore warehouseStore;

	public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
		this.warehouseStore = warehouseStore;
	}

	@Override
	@Transactional
	public void replace(Warehouse newWarehouse) {

		if (newWarehouse == null) {
			throw new WebApplicationException("Replacement warehouse cannot be null.",400);
		}

		// Find the currently active warehouse using the BU code.
		Warehouse currentWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

		if (currentWarehouse == null) {
			throw new WebApplicationException(
					"No active warehouse found for business unit code " + newWarehouse.businessUnitCode,400);
		}

		if (currentWarehouse.archivedAt != null) {
			throw new WebApplicationException("Warehouse " + newWarehouse.businessUnitCode + " is already archived.",
					400);
		}

		if (newWarehouse.capacity == null || newWarehouse.capacity < 0) {
			throw new WebApplicationException("Replacement warehouse capacity must be non-negative.", 400);
		}

		if (newWarehouse.stock == null || newWarehouse.stock < 0) {
			throw new WebApplicationException("Replacement warehouse stock must be non-negative.", 400);
		}

		// The replacement must be able to hold all stock
		// that exists in the warehouse being replaced.
		if (newWarehouse.capacity < currentWarehouse.stock) {
			throw new WebApplicationException(
					"Replacement warehouse capacity cannot accommodate the stock of the existing warehouse.", 400);
		}

		// Stock must remain exactly the same during replacement.
		if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
			throw new WebApplicationException("Replacement warehouse stock must match " + currentWarehouse.stock
					+ "the stock of the existing warehouse.", 400);
		}

		// The new warehouse must use the same Business Unit Code.
		if (!newWarehouse.businessUnitCode.equals(currentWarehouse.businessUnitCode)) {
			throw new WebApplicationException("Replacement warehouse must use the same business unit code.", 400);
		}

		// Archive the current warehouse.
		currentWarehouse.archivedAt = LocalDateTime.now();
		warehouseStore.update(currentWarehouse);

		// Create the replacement warehouse.
		newWarehouse.createdAt = LocalDateTime.now();
		newWarehouse.archivedAt = null;

		warehouseStore.create(newWarehouse);
	}
}