package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.model.Fulfillment;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "fulfillment",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_fulfillment_store_product_warehouse",
                        columnNames = {
                                "storeId",
                                "productId",
                                "warehouseId"
                        })
        })
public class DbFulfillment extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long storeId;

    @Column(nullable = false)
    public Long productId;

    @Column(nullable = false)
    public Long warehouseId;

    public Fulfillment toDomain() {

        Fulfillment fulfillment =
                new Fulfillment();

        fulfillment.id = id;
        fulfillment.storeId = storeId;
        fulfillment.productId = productId;
        fulfillment.warehouseId = warehouseId;

        return fulfillment;
    }

    public static DbFulfillment fromDomain(
            Fulfillment fulfillment) {

        DbFulfillment entity =
                new DbFulfillment();

        entity.id = fulfillment.id;
        entity.storeId = fulfillment.storeId;
        entity.productId = fulfillment.productId;
        entity.warehouseId = fulfillment.warehouseId;

        return entity;
    }
}