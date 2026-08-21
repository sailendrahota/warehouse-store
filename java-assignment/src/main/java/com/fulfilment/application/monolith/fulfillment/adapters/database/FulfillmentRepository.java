package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class FulfillmentRepository implements FulfillmentStore, PanacheRepository<DbFulfillment> {

    private final ProductRepository productRepository;
    private final WarehouseStore warehouseStore;

    public FulfillmentRepository(ProductRepository productRepository, WarehouseStore warehouseStore) {

        this.productRepository = productRepository;
        this.warehouseStore = warehouseStore;
    }

    @Override
    public boolean productExists(Long productId) {
        return productRepository.findById(productId) != null;
    }

    @Override
    public boolean storeExists(Long storeId) {
        return Store.findById(storeId) != null;
    }

    @Override
    public boolean warehouseExists(Long warehouseId) {
        Warehouse warehouse =
                warehouseStore.findActiveById(warehouseId);

        return warehouse != null;
    }

    @Override
    public boolean exists(Long storeId, Long productId, Long warehouseId) {

        return find(
                "storeId = ?1 and productId = ?2 and warehouseId = ?3",
                storeId,
                productId,
                warehouseId)
                .firstResult() != null;
    }

    @Override
    public long countWarehousesForProductAndStore(
            Long storeId,
            Long productId) {

        return find(
                "storeId = ?1 and productId = ?2",
                storeId,
                productId)
                .stream()
                .map(f -> f.warehouseId)
                .distinct()
                .count();
    }

    @Override
    public long countWarehousesForStore(Long storeId) {

        return find(
                "storeId = ?1",
                storeId)
                .stream()
                .map(f -> f.warehouseId)
                .distinct()
                .count();
    }

    @Override
    public long countProductsForWarehouse(Long warehouseId) {

        return find(
                "warehouseId = ?1",
                warehouseId)
                .stream()
                .map(f -> f.productId)
                .distinct()
                .count();
    }

    @Override
    public void create(Fulfillment fulfillment) {

        DbFulfillment entity =
                DbFulfillment.fromDomain(fulfillment);

        persistAndFlush(entity);

        fulfillment.id = entity.id;
    }

    @Override
    public List<Fulfillment> getAll() {

        return listAll()
                .stream()
                .map(DbFulfillment::toDomain)
                .toList();
    }
}