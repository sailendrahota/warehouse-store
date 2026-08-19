package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import jakarta.ws.rs.WebApplicationException;


class CreateWarehouseUseCaseTest {

	@Test
	void shouldCreateWarehouseWhenAllValidationsPass() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();
		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse warehouse = warehouse("MWH.100", "ZWOLLE-001", 40, 20);

		useCase.create(warehouse);

		assertEquals(1, warehouseStore.createdWarehouses.size());
		assertEquals("MWH.100", warehouseStore.createdWarehouses.get(0).businessUnitCode);
	}

	@Test
	void shouldRejectDuplicateActiveBusinessUnitCode() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		Warehouse existing = warehouse("MWH.100", "ZWOLLE-001", 40, 20);

		warehouseStore.warehouses.add(existing);

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse newWarehouse = warehouse("MWH.100", "ZWOLLE-001", 30, 10);

		WebApplicationException exception=assertThrows(WebApplicationException.class, () -> useCase.create(newWarehouse));
		assertEquals(404, exception.getResponse().getStatus());
		
	}

	@Test
	void shouldRejectInvalidLocation() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		FakeLocationResolver locationResolver = new FakeLocationResolver(null);

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse warehouse = warehouse("MWH.100", "UNKNOWN", 30, 10);

		WebApplicationException exception=assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));
		assertEquals(404, exception.getResponse().getStatus());
	}

	@Test
	void shouldRejectWhenMaximumNumberOfWarehousesReached() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		warehouseStore.warehouses.add(warehouse("MWH.001", "ZWOLLE-001", 20, 10));

		warehouseStore.warehouses.add(warehouse("MWH.002", "ZWOLLE-001", 20, 10));

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse newWarehouse = warehouse("MWH.003", "ZWOLLE-001", 20, 10);

		WebApplicationException exception=assertThrows(WebApplicationException.class, () -> useCase.create(newWarehouse));
		assertEquals(404, exception.getResponse().getStatus());
	}

	@Test
	void shouldRejectWhenWarehouseCapacityExceedsLocationCapacity() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 50));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse warehouse = warehouse("MWH.100", "ZWOLLE-001", 60, 20);

		WebApplicationException exception= assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));
		assertEquals(404, exception.getResponse().getStatus());
	}

	@Test
	void shouldRejectWhenStockExceedsWarehouseCapacity() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse warehouse = warehouse("MWH.100", "ZWOLLE-001", 40, 50);

		WebApplicationException exception=assertThrows(WebApplicationException.class, () -> useCase.create(warehouse));
		assertEquals(400, exception.getResponse().getStatus());
	}

	@Test
	void shouldRejectWhenTotalLocationCapacityIsExceeded() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		warehouseStore.warehouses.add(warehouse("MWH.001", "ZWOLLE-001", 70, 20));

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 3, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse warehouse = warehouse("MWH.002", "ZWOLLE-001", 40, 20);

		WebApplicationException exception= assertThrows(WebApplicationException.class,() -> useCase.create(warehouse));
		assertEquals(404, exception.getResponse().getStatus());
	}

	@Test
	void shouldAllowSameBusinessUnitCodeWhenPreviousWarehouseIsArchived() {

		FakeWarehouseStore warehouseStore = new FakeWarehouseStore();

		Warehouse archivedWarehouse = warehouse("MWH.100", "ZWOLLE-001", 40, 20);

		archivedWarehouse.archivedAt = java.time.LocalDateTime.now();

		warehouseStore.warehouses.add(archivedWarehouse);

		FakeLocationResolver locationResolver = new FakeLocationResolver(new Location("ZWOLLE-001", 2, 100));

		CreateWarehouseUseCase useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver);

		Warehouse replacement = warehouse("MWH.100", "ZWOLLE-001", 40, 20);

		useCase.create(replacement);

		assertEquals(1, warehouseStore.createdWarehouses.size());
	}

	private Warehouse warehouse(String businessUnitCode, String location, int capacity, int stock) {

		Warehouse warehouse = new Warehouse();

		warehouse.businessUnitCode = businessUnitCode;
		warehouse.location = location;
		warehouse.capacity = capacity;
		warehouse.stock = stock;

		return warehouse;
	}

	static class FakeLocationResolver implements LocationResolver {

		private final Location location;

		FakeLocationResolver(Location location) {
			this.location = location;
		}

		@Override
		public Location resolveByIdentifier(String identifier) {
			return location;
		}
	}

	static class FakeWarehouseStore implements WarehouseStore {

		List<Warehouse> warehouses = new ArrayList<>();
		List<Warehouse> createdWarehouses = new ArrayList<>();

		@Override
		public List<Warehouse> getAll() {
			return warehouses;
		}

		@Override
		public void create(Warehouse warehouse) {
			createdWarehouses.add(warehouse);
			warehouses.add(warehouse);
		}

		@Override
		public void update(Warehouse warehouse) {
			// Not required for CreateWarehouseUseCase tests
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
