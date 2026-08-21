package com.fulfilment.application.monolith.warehouses.domain.validation;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WarehouseCreationValidator {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public WarehouseCreationValidator(
            WarehouseStore warehouseStore,
            LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    public void validate(Warehouse warehouse) {

        if (warehouse == null) {
            throw new WarehouseValidationException(
                    "Warehouse cannot be null.");
        }

        // 1. Business Unit Code must be unique among active warehouses
        boolean businessUnitCodeAlreadyExists =
                warehouseStore.getAll().stream()
                        .anyMatch(existing ->
                                existing.archivedAt == null
                                        && existing.businessUnitCode.equals(
                                        warehouse.businessUnitCode));

        if (businessUnitCodeAlreadyExists) {
            throw new WarehouseValidationException(
                    "Warehouse with business unit code "
                            + warehouse.businessUnitCode
                            + " already exists.");
        }

        // 2. Location must exist
        Location location =
                locationResolver.resolveByIdentifier(warehouse.location);

        if (location == null) {
            throw new WarehouseValidationException(
                    "Location " + warehouse.location + " does not exist.");
        }

        // Only active warehouses count towards location limits.
        List<Warehouse> activeWarehousesAtLocation =
                warehouseStore.getAll().stream()
                        .filter(existing ->
                                existing.archivedAt == null
                                        && existing.location.equals(
                                        warehouse.location))
                        .toList();

        // 3. Maximum number of warehouses at this location
        if (activeWarehousesAtLocation.size()
                >= location.maxNumberOfWarehouses) {

            throw new WarehouseValidationException(
                    "Maximum number of warehouses reached for location "
                            + warehouse.location);
        }

        // 4. Warehouse capacity validation
        if (warehouse.capacity == null || warehouse.capacity < 0) {
            throw new WarehouseValidationException(
                    "Warehouse capacity must be a non-negative value.");
        }

        if (warehouse.capacity > location.maxCapacity) {
            throw new WarehouseValidationException(
                    "Warehouse capacity exceeds the maximum capacity of location "
                            + warehouse.location);
        }

        // 5. Stock validation
        if (warehouse.stock == null || warehouse.stock < 0) {
            throw new WarehouseValidationException(
                    "Warehouse stock must be a non-negative value.");
        }

        if (warehouse.stock > warehouse.capacity) {
            throw new WarehouseValidationException(
                    "Warehouse stock cannot exceed warehouse capacity.");
        }

        // 6. Total capacity at location
        int currentTotalCapacity =
                activeWarehousesAtLocation.stream()
                        .mapToInt(existing -> existing.capacity)
                        .sum();

        if (currentTotalCapacity + warehouse.capacity
                > location.maxCapacity) {

            throw new WarehouseValidationException(
                    "Total warehouse capacity exceeds the maximum capacity of location "
                            + warehouse.location);
        }
    }
}