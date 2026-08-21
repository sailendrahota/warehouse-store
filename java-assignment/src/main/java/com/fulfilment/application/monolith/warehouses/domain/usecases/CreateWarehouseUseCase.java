package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validation.WarehouseCreationValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

	private final WarehouseStore warehouseStore;
	private final WarehouseCreationValidator validator;

	public CreateWarehouseUseCase(
			WarehouseStore warehouseStore,
		WarehouseCreationValidator validator) {
		this.warehouseStore = warehouseStore;
		this.validator = validator;
	}

	@Override
	@Transactional
	public void create(Warehouse warehouse) {
		validator.validate(warehouse);
		warehouseStore.create(warehouse);
	}
}