package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

	@Override
	public List<Warehouse> getAll() {
		return find("archivedAt is null").list().stream().map(DbWarehouse::toWarehouse).toList();
	}

	@Override
	public void create(Warehouse warehouse) {
		DbWarehouse dbWarehouse = new DbWarehouse();

		dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
		dbWarehouse.location = warehouse.location;
		dbWarehouse.capacity = warehouse.capacity;
		dbWarehouse.stock = warehouse.stock;
		dbWarehouse.createdAt = warehouse.createdAt;
		dbWarehouse.archivedAt = warehouse.archivedAt;

		persistAndFlush(dbWarehouse);
		warehouse.id = dbWarehouse.id;
	}

	@Override
	public void update(Warehouse warehouse) {
		DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode)
				.firstResult();

		if (dbWarehouse == null) {
			return;
		}

		dbWarehouse.location = warehouse.location;
		dbWarehouse.capacity = warehouse.capacity;
		dbWarehouse.stock = warehouse.stock;
		dbWarehouse.createdAt = warehouse.createdAt;
		dbWarehouse.archivedAt = warehouse.archivedAt;
	}

	@Override
	public void remove(Warehouse warehouse) {
		DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", warehouse.businessUnitCode)
				.firstResult();

		if (dbWarehouse != null) {
			delete(dbWarehouse);
		}
	}

	@Override
	public Warehouse findByBusinessUnitCode(String buCode) {
		DbWarehouse dbWarehouse = find("businessUnitCode = ?1 and archivedAt is null", buCode).firstResult();

		return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
	}

	@Override
	public Warehouse findActiveById(Long id) {
		DbWarehouse dbWarehouse = find("id = ?1 and archivedAt is null", id).firstResult();
		return dbWarehouse != null ? dbWarehouse.toWarehouse() : null;
	}

}