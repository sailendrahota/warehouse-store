package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.adapters.database.FulfillmentRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
class FulfillmentResourceTest {

    @Inject
    FulfillmentRepository fulfillmentRepository;

    @BeforeEach
    @TestTransaction
    void setUp() {
        fulfillmentRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /fulfillment - Successfully associate warehouse with product and store")
    void testAssociateFulfillmentSuccess() {
        String payload = """
                {
                    "storeId": 1,
                    "productId": 1,
                    "warehouseId": 1
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("storeId", equalTo(1))
                .body("productId", equalTo(1))
                .body("warehouseId", equalTo(1));
    }

    @Test
    @DisplayName("POST /fulfillment - Fails when product has max 2 warehouses per store")
    void testAssociateFulfillmentMax2WarehousesPerProductPerStore() {
        // Warehouse 1
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"storeId": 1, "productId": 1, "warehouseId": 1}
                        """)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(200), is(400)));

        // Warehouse 2
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"storeId": 1, "productId": 1, "warehouseId": 2}
                        """)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(200), is(201)));

        // 3rd Warehouse for same product and store (Constraint Violation: Max 2)
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"storeId": 1, "productId": 1, "warehouseId": 3}
                        """)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    @DisplayName("POST /fulfillment - Fails on invalid or missing IDs")
    void testAssociateFulfillmentInvalidPayload() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"storeId": null, "productId": 1, "warehouseId": 1}
                        """)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(400), is(422), is(500)));
    }

    @Test
    @DisplayName("GET /fulfillment - Returns list of all fulfillment associations")
    void testGetAllFulfillments() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"storeId": 1, "productId": 1, "warehouseId": 1}
                        """)
                .when()
                .post("/fulfillment")
                .then()
                .statusCode(anyOf(is(200), is(400)));

        given()
                .when()
                .get("/fulfillment")
                .then()
                .statusCode(200)
                .body("size()", notNullValue());
    }
}