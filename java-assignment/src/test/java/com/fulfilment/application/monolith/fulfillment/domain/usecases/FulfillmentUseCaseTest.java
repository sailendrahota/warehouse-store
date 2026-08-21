package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validation.FulfillmentValidator;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FulfillmentUseCaseTest {

    @Test
    void shouldCreateFulfillmentWhenValid() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = true;
        store.productExists = true;
        store.warehouseExists = true;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        AssociateFulfillmentUseCase useCase =
                new AssociateFulfillmentUseCase(
                        store,
                        validator);

        Fulfillment fulfillment =
                fulfillment(1L, 1L, 1L);

        Fulfillment result =
                useCase.associate(fulfillment);

        assertNotNull(result);
        assertEquals(
                fulfillment,
                store.createdFulfillment);
    }

    @Test
    void shouldReturnAllFulfillments() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        Fulfillment first =
                fulfillment(1L, 1L, 1L);

        Fulfillment second =
                fulfillment(1L, 2L, 2L);

        store.fulfillments.add(first);
        store.fulfillments.add(second);

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        AssociateFulfillmentUseCase useCase =
                new AssociateFulfillmentUseCase(
                        store,
                        validator);

        List<Fulfillment> result =
                useCase.getAll();

        assertEquals(2, result.size());
        assertEquals(first, result.get(0));
        assertEquals(second, result.get(1));
    }

    @Test
    void shouldNotCreateFulfillmentWhenValidationFails() {

        FakeFulfillmentStore store =
                new FakeFulfillmentStore();

        store.storeExists = true;
        store.productExists = true;
        store.warehouseExists = true;

        store.warehousesForProductAndStore = 2;

        FulfillmentValidator validator =
                new FulfillmentValidator(store);

        AssociateFulfillmentUseCase useCase =
                new AssociateFulfillmentUseCase(
                        store,
                        validator);

        assertThrows(
                RuntimeException.class,
                () -> useCase.associate(
                        fulfillment(1L, 1L, 3L)));

        assertEquals(
                null,
                store.createdFulfillment);
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

        long warehousesForProductAndStore;
        long warehousesForStore;
        long productsForWarehouse;

        List<Fulfillment> fulfillments =
                new ArrayList<>();

        Fulfillment createdFulfillment;

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
            return false;
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
            createdFulfillment = fulfillment;
            fulfillments.add(fulfillment);
        }

        @Override
        public List<Fulfillment> getAll() {
            return fulfillments;
        }
    }
}
