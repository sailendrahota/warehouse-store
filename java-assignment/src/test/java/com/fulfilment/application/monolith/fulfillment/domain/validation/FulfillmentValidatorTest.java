package com.fulfilment.application.monolith.fulfillment.domain.validation;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FulfillmentValidatorTest {

    @Test
    void shouldAcceptValidFulfillment() {

        FakeFulfillmentStore store = new FakeFulfillmentStore();

        store.productExists = true;
        store.storeExists = true;
        store.warehouseExists = true;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        Fulfillment fulfillment =
                fulfillment(1L, 1L, 1L);

        assertDoesNotThrow(
                () -> validator.validate(fulfillment));
    }

    @Test
    void shouldRejectNullFulfillment() {

        FulfillmentValidator validator =
                new FulfillmentValidator(
                        new FakeFulfillmentStore());

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(null));

        assertEquals(
                "Fulfillment request cannot be null.",
                exception.getMessage());
    }

    @Test
    void shouldRejectNullStoreId() {

        FulfillmentValidator validator =
                new FulfillmentValidator(
                        new FakeFulfillmentStore());

        Fulfillment fulfillment =
                fulfillment(null, 1L, 1L);

        assertThrows(
                FulfillmentValidationException.class,
                () -> validator.validate(fulfillment));
    }

    @Test
    void shouldRejectNullProductId() {

        FulfillmentValidator validator =
                new FulfillmentValidator(
                        new FakeFulfillmentStore());

        Fulfillment fulfillment =
                fulfillment(1L, null, 1L);

        assertThrows(
                FulfillmentValidationException.class,
                () -> validator.validate(fulfillment));
    }

    @Test
    void shouldRejectNullWarehouseId() {

        FulfillmentValidator validator =
                new FulfillmentValidator(
                        new FakeFulfillmentStore());

        Fulfillment fulfillment =
                fulfillment(1L, 1L, null);

        assertThrows(
                FulfillmentValidationException.class,
                () -> validator.validate(fulfillment));
    }

    @Test
    void shouldRejectUnknownStore() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = false;
        store.productExists = true;
        store.warehouseExists = true;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 1L)));

        assertEquals(
                "Store with id 1 does not exist.",
                exception.getMessage());
    }

    @Test
    void shouldRejectUnknownProduct() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = true;
        store.productExists = false;
        store.warehouseExists = true;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 1L)));

        assertEquals(
                "Product with id 1 does not exist.",
                exception.getMessage());
    }

    @Test
    void shouldRejectUnknownWarehouse() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = true;
        store.productExists = true;
        store.warehouseExists = false;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 1L)));

        assertEquals(
                "Active warehouse with id 1 does not exist.",
                exception.getMessage());
    }

    @Test
    void shouldRejectDuplicateFulfillment() {

        FakeFulfillmentStore store =
                validStore();

        store.associationExists = true;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 1L)));

        assertEquals(
                "Fulfillment association already exists.",
                exception.getMessage());
    }

    @Test
    void shouldRejectThirdWarehouseForSameProductAndStore() {

        FakeFulfillmentStore store =
                validStore();

        store.warehousesForProductAndStore = 2;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 3L)));

        assertEquals(
                "A product can be fulfilled by a maximum of 2 warehouses per store.",
                exception.getMessage());
    }

    @Test
    void shouldRejectFourthWarehouseForSameStore() {

        FakeFulfillmentStore store =
                validStore();

        store.warehousesForStore = 3;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 1L, 4L)));

        assertEquals(
                "A store can be fulfilled by a maximum of 3 warehouses.",
                exception.getMessage());
    }

    @Test
    void shouldRejectSixthProductForSameWarehouse() {

        FakeFulfillmentStore store =
                validStore();

        store.productsForWarehouse = 5;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        FulfillmentValidationException exception =
                assertThrows(
                        FulfillmentValidationException.class,
                        () -> validator.validate(
                                fulfillment(1L, 6L, 1L)));

        assertEquals(
                "A warehouse can store a maximum of 5 product types.",
                exception.getMessage());
    }

    private FakeFulfillmentStore validStore() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = true;
        store.productExists = true;
        store.warehouseExists = true;

        return store;
    }

    private Fulfillment fulfillment(
            Long storeId,
            Long productId,
            Long warehouseId) {

        Fulfillment fulfillment =
                new Fulfillment();

        fulfillment.storeId = storeId;
        fulfillment.productId = productId;
        fulfillment.warehouseId = warehouseId;

        return fulfillment;
    }

    static class FakeFulfillmentStore
            implements FulfillmentStore {

        boolean storeExists;
        boolean productExists;
        boolean warehouseExists;
        boolean associationExists;

        long warehousesForProductAndStore;
        long warehousesForStore;
        long productsForWarehouse;

        @Override
        public boolean productExists(Long productId) {
            return productExists;
        }

        @Override
        public boolean storeExists(Long storeId) {
            return storeExists;
        }

        @Override
        public boolean warehouseExists(Long warehouseId) {
            return warehouseExists;
        }

        @Override
        public boolean exists(
                Long storeId,
                Long productId,
                Long warehouseId) {
            return associationExists;
        }

        @Override
        public long countWarehousesForProductAndStore(
                Long storeId,
                Long productId) {
            return warehousesForProductAndStore;
        }

        @Override
        public long countWarehousesForStore(
                Long storeId) {
            return warehousesForStore;
        }

        @Override
        public long countProductsForWarehouse(
                Long warehouseId) {
            return productsForWarehouse;
        }

        @Override
        public void create(Fulfillment fulfillment) {
        }

        @Override
        public List<Fulfillment> getAll() {
            return Collections.emptyList();
        }
    }
}
