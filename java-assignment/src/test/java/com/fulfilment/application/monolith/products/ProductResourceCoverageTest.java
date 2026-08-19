package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProductResourceCoverageTest {

    @Inject
    ProductResource productResource;

    @Test
    void shouldGetProductsDirectly() {
        List<Product> products = productResource.get();

        assertNotNull(products);

    }

    @Test
    void shouldGetSingleProductDirectly() {
        Product product = productResource.getSingle(1L);

        assertNotNull(product);

    }

    @Test
    void shouldRejectUnknownProductDirectly() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.getSingle(99999L));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    @Transactional
    void shouldCreateProductDirectly() {
        Product product = new Product();
        product.name = "DIRECT-TEST";
        product.description = "Coverage";
        product.stock = 5;

        var response = productResource.create(product);

        assertEquals(201, response.getStatus());
        assertNotNull(product.id);
    }

    @Test
    void shouldRejectCreateWithIdDirectly() {
        Product product = new Product();
        product.id = 999L;
        product.name = "INVALID";

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.create(product));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    @Transactional
    void shouldUpdateProductDirectly() {
        Product updated = new Product();
        updated.name = "TONSTAD-DIRECT-UPDATED";
        updated.description = "Updated";
        updated.stock = 20;

        Product result = productResource.update(1L, updated);

        assertNotNull(result);
        assertEquals("TONSTAD-DIRECT-UPDATED", result.name);
    }

    @Test
    void shouldRejectUpdateWithoutNameDirectly() {
        Product updated = new Product();

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(1L, updated));

        assertEquals(422, exception.getResponse().getStatus());
    }

    @Test
    void shouldRejectUpdateForUnknownProductDirectly() {
        Product updated = new Product();
        updated.name = "UNKNOWN";

        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.update(99999L, updated));

        assertEquals(404, exception.getResponse().getStatus());
    }

    @Test
    @Transactional
    void shouldDeleteProductDirectly() {
        Product product = productResource.getSingle(1L);

        assertNotNull(product);

        var response = productResource.delete(1L);

        assertEquals(204, response.getStatus());
    }

    @Test
    void shouldRejectDeleteForUnknownProductDirectly() {
        WebApplicationException exception =
                assertThrows(
                        WebApplicationException.class,
                        () -> productResource.delete(99999L));

        assertEquals(404, exception.getResponse().getStatus());
    }
}
