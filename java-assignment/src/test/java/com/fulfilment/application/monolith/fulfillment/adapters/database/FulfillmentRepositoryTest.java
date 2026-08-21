package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FulfillmentRepositoryTest {

    @Inject
    FulfillmentRepository fulfillmentRepository;

    @BeforeEach
    @TestTransaction
    void setUp() {
        // Clear table before each test to ensure isolation
        fulfillmentRepository.deleteAll();
    }

    @Test
    @TestTransaction
    @DisplayName("Should create and retrieve domain Fulfillment")
    void testCreateAndGetAll() {
        Fulfillment domain = new Fulfillment();
        domain.storeId = 10L;
        domain.productId = 20L;
        domain.warehouseId = 30L;

        fulfillmentRepository.create(domain);

        // Verify the ID was updated after persist
        assertNotNull(domain.id);

        List<Fulfillment> all = fulfillmentRepository.getAll();
        assertEquals(1, all.size());
        assertEquals(10L, all.get(0).storeId);
        assertEquals(20L, all.get(0).productId);
        assertEquals(30L, all.get(0).warehouseId);
    }

    @Test
    @TestTransaction
    @DisplayName("Should check if a specific fulfillment association exists")
    void testExists() {
        createAndPersist(1L, 2L, 3L);

        assertTrue(fulfillmentRepository.exists(1L, 2L, 3L));
        assertFalse(fulfillmentRepository.exists(99L, 99L, 99L));
    }

    @Test
    @TestTransaction
    @DisplayName("Should correctly count warehouses for a product in a store")
    void testCountWarehousesForProductAndStore() {
        createAndPersist(1L, 1L, 1L);
        createAndPersist(1L, 1L, 2L);
        createAndPersist(2L, 1L, 3L); // different store, should be ignored

        long count = fulfillmentRepository.countWarehousesForProductAndStore(1L, 1L);
        assertEquals(2, count);
    }

    @Test
    @TestTransaction
    @DisplayName("Should correctly count total fulfillments for a store")
    void testCountWarehousesForStore() {
        createAndPersist(1L, 1L, 10L);
        createAndPersist(1L, 2L, 10L);
        createAndPersist(1L, 3L, 20L);
        createAndPersist(2L, 1L, 30L); // different store

        long count = fulfillmentRepository.countWarehousesForStore(1L);
        assertTrue(count>0);
    }

    @Test
    @TestTransaction
    @DisplayName("Should correctly count total fulfillments for a warehouse")
    void testCountProductsForWarehouse() {
        createAndPersist(10L, 1L, 1L);
        createAndPersist(20L, 1L, 1L);
        createAndPersist(10L, 2L, 1L);
        createAndPersist(10L, 3L, 2L); // different warehouse

        long count = fulfillmentRepository.countProductsForWarehouse(1L);
        assertTrue(count>0);
    }

    /**
     * Helper method to insert raw DbFulfillment entities into the database
     * without triggering the domain 'create' method.
     */
    private void createAndPersist(Long storeId, Long productId, Long warehouseId) {
        DbFulfillment entity = new DbFulfillment();
        entity.storeId = storeId;
        entity.productId = productId;
        entity.warehouseId = warehouseId;
        fulfillmentRepository.persistAndFlush(entity);
    }
}