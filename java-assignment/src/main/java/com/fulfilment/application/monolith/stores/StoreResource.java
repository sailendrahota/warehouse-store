package com.fulfilment.application.monolith.stores;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionSynchronizationRegistry;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.List;

@Path("store")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class StoreResource {

    private static final Logger LOGGER = Logger.getLogger(StoreResource.class.getName());
    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;
    @Inject
    Event<StoreChangedEvent> storeChangedEvent;


    @GET
    public List<Store> get() {
        return Store.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Store getSingle(Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }
        return entity;
    }

    @POST
    @Transactional
    public Response create(Store store) {
        if (store.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        store.persist();
        storeChangedEvent.fire( new StoreChangedEvent( store, StoreChangedEvent.ChangeType.CREATED));

        return Response.ok(store).status(201).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    public Store update(Long id, Store updatedStore) {
        if (updatedStore.name == null) {
            throw new WebApplicationException("Store Name was not set on request.", 422);
        }

        Store entity = Store.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;

        storeChangedEvent.fire(new StoreChangedEvent(
                        entity,
                        StoreChangedEvent.ChangeType.UPDATED));

        return entity;
    }

    @PATCH
    @Path("{id}")
    @Transactional
    public Store patch(Long id, Store updatedStore) {
        if (updatedStore.name == null) {
            throw new WebApplicationException("Store Name was not set on request.", 422);
        }

        Store entity = Store.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }

        if (entity.name != null) {
            entity.name = updatedStore.name;
        }

        if (entity.quantityProductsInStock != 0) {
            entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
        }
        storeChangedEvent.fire(new StoreChangedEvent(
                entity,
                StoreChangedEvent.ChangeType.UPDATED));

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Store with id of " + id + " does not exist.", 404);
        }
        entity.delete();
        return Response.status(204).build();
    }



    }
