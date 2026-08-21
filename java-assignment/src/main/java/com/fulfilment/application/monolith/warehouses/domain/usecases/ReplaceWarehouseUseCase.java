package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseReplacementValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

	private final WarehouseStore warehouseStore;
	private final WarehouseReplacementValidator validator;

	public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, WarehouseReplacementValidator validator) {
		this.warehouseStore = warehouseStore;
		this.validator = validator;
	}

	@Override
	@Transactional
	public void replace(Warehouse newWarehouse) {

		Warehouse currentWarehouse =
				validator.validate(newWarehouse);

		currentWarehouse.archivedAt = LocalDateTime.now();
		warehouseStore.update(currentWarehouse);

		newWarehouse.createdAt = LocalDateTime.now();
		newWarehouse.archivedAt = null;

		warehouseStore.create(newWarehouse);
	}
}