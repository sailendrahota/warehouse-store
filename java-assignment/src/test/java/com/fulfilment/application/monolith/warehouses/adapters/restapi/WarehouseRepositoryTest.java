package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.TestTransaction;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseRepositoryTest {

	@Inject
	WarehouseRepository warehouseRepository;

	@Test
	void shouldReturnOnlyActiveWarehouses() {

		List<Warehouse> warehouses = warehouseRepository.getAll();

		assertNotNull(warehouses);
		assertTrue(warehouses.stream().allMatch(w -> w.archivedAt == null));
		assertTrue(warehouses.stream().anyMatch(w -> "MWH.001".equals(w.businessUnitCode)));
	}

	@Test
	@TestTransaction
	void shouldCreateWarehouseAndAssignGeneratedId() {

		Warehouse warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.REPO.TEST";
		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 20;
		warehouse.stock = 5;
		warehouse.createdAt = LocalDateTime.now();

		warehouseRepository.create(warehouse);

		assertNotNull(warehouse.id);

		Warehouse saved = warehouseRepository.findActiveById(warehouse.id);

		assertNotNull(saved);
		assertEquals("MWH.REPO.TEST", saved.businessUnitCode);
		assertEquals("AMSTERDAM-001", saved.location);
		assertEquals(20, saved.capacity);
		assertEquals(5, saved.stock);
	}

	@Test
	void shouldFindActiveWarehouseById() {

		Warehouse warehouse = warehouseRepository.findActiveById(1L);

		assertNotNull(warehouse);
		assertEquals("MWH.001", warehouse.businessUnitCode);
		assertEquals(1L, warehouse.id);
	}

	@Test
	@TestTransaction
	void shouldNotFindArchivedWarehouseById() {

		Warehouse warehouse = warehouseRepository.findActiveById(1L);

		assertNotNull(warehouse);

		warehouse.archivedAt = LocalDateTime.now();

		warehouseRepository.update(warehouse);

		Warehouse result = warehouseRepository.findActiveById(1L);

		assertNull(result);
	}

	@Test
	void shouldFindActiveWarehouseByBusinessUnitCode() {

		Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.001");

		assertNotNull(warehouse);
		assertEquals("MWH.001", warehouse.businessUnitCode);
		assertNull(warehouse.archivedAt);
	}

	@Test
	void shouldReturnNullForUnknownBusinessUnitCode() {

		Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.UNKNOWN");

		assertNull(warehouse);
	}

	@Test
	@TestTransaction
	void shouldUpdateWarehouse() {

		Warehouse warehouse = warehouseRepository.findByBusinessUnitCode("MWH.001");

		assertNotNull(warehouse);

		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 80;
		warehouse.stock = 15;

		warehouseRepository.update(warehouse);

		Warehouse updated = warehouseRepository.findByBusinessUnitCode("MWH.001");

		assertNotNull(updated);
		assertEquals("AMSTERDAM-001", updated.location);
		assertEquals(80, updated.capacity);
		assertEquals(15, updated.stock);
	}

	@Test
	@TestTransaction
	void shouldRemoveWarehouse() {

		Warehouse warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.REPO.REMOVE";
		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 20;
		warehouse.stock = 5;

		warehouseRepository.create(warehouse);

		assertNotNull(warehouse.id);

		warehouseRepository.remove(warehouse);

		Warehouse deleted = warehouseRepository.findByBusinessUnitCode("MWH.REPO.REMOVE");

		assertNull(deleted);
	}

	@Test
	@TestTransaction
	void shouldIgnoreArchivedWarehouseWhenFindingByBusinessUnitCode() {

		Warehouse warehouse = new Warehouse();
		warehouse.businessUnitCode = "MWH.REPO.ARCHIVED";
		warehouse.location = "AMSTERDAM-001";
		warehouse.capacity = 20;
		warehouse.stock = 5;
		warehouse.archivedAt = LocalDateTime.now();

		warehouseRepository.create(warehouse);

		assertNotNull(warehouse.id);

		Warehouse result = warehouseRepository.findByBusinessUnitCode("MWH.REPO.ARCHIVED");

		assertNull(result);
	}
}
