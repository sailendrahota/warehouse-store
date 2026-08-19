package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;

import java.util.List;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

	private final WarehouseStore warehouseStore;
	private final LocationResolver locationResolver;

	public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
		this.warehouseStore = warehouseStore;
		this.locationResolver = locationResolver;
	}

	@Override
	@Transactional
	public void create(Warehouse warehouse) {

		// 1. Business Unit Code must be unique among active warehouses
		boolean businessUnitCodeAlreadyExists = warehouseStore.getAll().stream()
				.anyMatch(existing -> existing.archivedAt == null
						&& existing.businessUnitCode.equals(warehouse.businessUnitCode));

		if (businessUnitCodeAlreadyExists) {
			throw new  WebApplicationException(
					"Warehouse with business unit code " + warehouse.businessUnitCode + " already exists.",404);
		}

		// 2. Location must exist
		Location location = locationResolver.resolveByIdentifier(warehouse.location);

		if (location == null) {
			throw new  WebApplicationException("Location " + warehouse.location + " does not exist.",404);
		}

		// Only active warehouses should count towards location limits.
		List<Warehouse> activeWarehousesAtLocation = warehouseStore.getAll().stream()
				.filter(existing -> existing.archivedAt == null && existing.location.equals(warehouse.location))
				.toList();

		// 3. Maximum number of warehouses at this location
		if (activeWarehousesAtLocation.size() >= location.maxNumberOfWarehouses) {
			throw new  WebApplicationException(
					"Maximum number of warehouses reached for location " + warehouse.location,404);
		}

		// 4. Warehouse capacity must be valid
		if (warehouse.capacity == null || warehouse.capacity < 0) {
			throw new  WebApplicationException("Warehouse capacity must be a non-negative value.",400);
		}

		if (warehouse.capacity > location.maxCapacity) {
			throw new  WebApplicationException(
					"Warehouse capacity exceeds the maximum capacity of location " + warehouse.location,404);
		}

		// 5. Stock must fit inside warehouse capacity
		if (warehouse.stock == null || warehouse.stock < 0) {
			throw new  WebApplicationException("Warehouse stock must be a non-negative value.",400);
		}

		if (warehouse.stock > warehouse.capacity) {
			throw new  WebApplicationException("Warehouse stock cannot exceed warehouse capacity.",400);
		}

		// 6. Sum of all active warehouse capacities must stay
		// within the location's maximum capacity.
		int currentTotalCapacity = activeWarehousesAtLocation.stream().mapToInt(existing -> existing.capacity).sum();

		if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
			throw new  WebApplicationException(
					"Total warehouse capacity exceeds the maximum capacity of location " + warehouse.location,404);
		}

		// 7. Create only after all validations pass
		warehouseStore.create(warehouse);
	}
}