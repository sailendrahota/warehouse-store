package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
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
			throw new IllegalArgumentException("Replacement warehouse cannot be null.");
		}

		// Find the currently active warehouse using the BU code.
		Warehouse currentWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);

		if (currentWarehouse == null) {
			throw new IllegalArgumentException(
					"No active warehouse found for business unit code " + newWarehouse.businessUnitCode);
		}

		if (currentWarehouse.archivedAt != null) {
			throw new IllegalArgumentException("Warehouse " + newWarehouse.businessUnitCode + " is already archived.");
		}

		if (newWarehouse.capacity == null || newWarehouse.capacity < 0) {
			throw new IllegalArgumentException("Replacement warehouse capacity must be non-negative.");
		}

		if (newWarehouse.stock == null || newWarehouse.stock < 0) {
			throw new IllegalArgumentException("Replacement warehouse stock must be non-negative.");
		}

		// The replacement must be able to hold all stock
		// that exists in the warehouse being replaced.
		if (newWarehouse.capacity < currentWarehouse.stock) {
			throw new IllegalArgumentException(
					"Replacement warehouse capacity cannot accommodate " + "the stock of the existing warehouse.");
		}

		// Stock must remain exactly the same during replacement.
		if (!newWarehouse.stock.equals(currentWarehouse.stock)) {
			throw new IllegalArgumentException(
					"Replacement warehouse stock must match " + "the stock of the existing warehouse.");
		}

		// The new warehouse must use the same Business Unit Code.
		if (!newWarehouse.businessUnitCode.equals(currentWarehouse.businessUnitCode)) {
			throw new IllegalArgumentException("Replacement warehouse must use the same business unit code.");
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