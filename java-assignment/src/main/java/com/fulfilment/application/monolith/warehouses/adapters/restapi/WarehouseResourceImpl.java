package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import java.util.List;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.warehouse.api.WarehouseResource;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

	@Inject
	WarehouseStore warehouseStore;

	@Inject
	CreateWarehouseOperation createWarehouseOperation;

	@Inject
	ArchiveWarehouseOperation archiveWarehouseOperation;

	@Inject
	ReplaceWarehouseOperation replaceWarehouseOperation;

	@Override
	public List<com.warehouse.api.beans.Warehouse> listAllWarehousesUnits() {
		return warehouseStore.getAll().stream().map(this::toWarehouseResponse).toList();
	}

	@Override
	public com.warehouse.api.beans.Warehouse createANewWarehouseUnit(@NotNull com.warehouse.api.beans.Warehouse data) {

		Warehouse warehouse = toDomain(data);

		createWarehouseOperation.create(warehouse);

		return toWarehouseResponse(warehouse);
	}

	@Override
	public com.warehouse.api.beans.Warehouse getAWarehouseUnitByID(String id) {

		Warehouse warehouse = warehouseStore.findByBusinessUnitCode(id);
		if (warehouse == null) {
			throw new IllegalArgumentException("Warehouse with business unit code " + id + " not found.");
		}

		return toWarehouseResponse(warehouse);
	}

	@Override
	public void archiveAWarehouseUnitByID(String id) {

		Warehouse warehouse = warehouseStore.findByBusinessUnitCode(id);

		if (warehouse == null) {
			throw new IllegalArgumentException("Warehouse with business unit code " + id + " not found.");
		}

		archiveWarehouseOperation.archive(warehouse);
	}

	@Override
	public com.warehouse.api.beans.Warehouse replaceTheCurrentActiveWarehouse(String businessUnitCode,
			@NotNull com.warehouse.api.beans.Warehouse data) {

		Warehouse warehouse = toDomain(data);

		// The path variable is authoritative.
		warehouse.businessUnitCode = businessUnitCode;

		replaceWarehouseOperation.replace(warehouse);

		return toWarehouseResponse(warehouse);
	}

	private Warehouse toDomain(com.warehouse.api.beans.Warehouse data) {

		Warehouse warehouse = new Warehouse();

		warehouse.businessUnitCode = data.getBusinessUnitCode();
		warehouse.location = data.getLocation();
		warehouse.capacity = data.getCapacity();
		warehouse.stock = data.getStock();

		return warehouse;
	}

	private com.warehouse.api.beans.Warehouse toWarehouseResponse(Warehouse warehouse) {

		var response = new com.warehouse.api.beans.Warehouse();

		response.setBusinessUnitCode(warehouse.businessUnitCode);
		response.setLocation(warehouse.location);
		response.setCapacity(warehouse.capacity);
		response.setStock(warehouse.stock);

		return response;
	}
}