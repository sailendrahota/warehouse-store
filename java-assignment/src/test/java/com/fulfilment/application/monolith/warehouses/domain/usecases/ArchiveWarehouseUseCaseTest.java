package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.ws.rs.WebApplicationException;


class ArchiveWarehouseUseCaseTest {

	@Test
	void shouldArchiveActiveWarehouse() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(warehouseStore);

		Warehouse warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.100";

		useCase.archive(warehouse);

		assertNotNull(warehouse.archivedAt);
	}

	@Test
	void shouldRejectNullWarehouse() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(warehouseStore);

		WebApplicationException exception =assertThrows(WebApplicationException.class, () -> useCase.archive(null));
		assertEquals(400, exception.getResponse().getStatus());
	}

	@Test
	void shouldRejectAlreadyArchivedWarehouse() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		ArchiveWarehouseUseCase useCase = new ArchiveWarehouseUseCase(warehouseStore);

		Warehouse warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.100";
		warehouse.archivedAt = java.time.LocalDateTime.now();

		WebApplicationException exception=assertThrows(WebApplicationException.class, () -> useCase.archive(warehouse));
		assertEquals(404, exception.getResponse().getStatus());
	}

	static class FakeWarehouseStore implements WarehouseStore {

		List<Warehouse> warehouses = new ArrayList<>();

		@Override
		public List<Warehouse> getAll() {
			return warehouses;
		}

		@Override
		public void create(Warehouse warehouse) {
			warehouses.add(warehouse);
		}

		@Override
		public void update(Warehouse warehouse) {
			warehouses.add(warehouse);
		}

		@Override
		public void remove(Warehouse warehouse) {
			warehouses.remove(warehouse);
		}

		@Override
		public Warehouse findByBusinessUnitCode(String buCode) {
			return warehouses.stream().filter(w -> w.businessUnitCode.equals(buCode)).findFirst().orElse(null);
		}

		@Override
		public Warehouse findActiveById(Long id) {
			return warehouses.stream().filter(w -> w.id != null && w.id.equals(id)).filter(w -> w.archivedAt == null)
					.findFirst().orElse(null);
		}
	}
}