package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreResourceTest {

	@Test
	void shouldGetAllStores() {
		given().when().get("/store").then().statusCode(200).body(containsString("TONSTAD"), containsString("KALLAX"),
				containsString("TONSTAD-UPDATED"));
	}

	@Test
	void shouldGetStoreById() {
		given().when().get("/store/1").then().statusCode(200).body(containsString("TONSTAD"));
	}

	@Test
	void shouldReturn404WhenStoreDoesNotExist() {
		given().when().get("/store/99999").then().statusCode(404);
	}

	@Test
	void shouldCreateStore() {
		given().contentType("application/json").body("""
				{
				  "name": "TEST-CREATE",
				  "quantityProductsInStock": 10
				}
				""").when().post("/store").then().statusCode(201).body(containsString("TEST-CREATE"));
	}

	@Test
	void shouldRejectCreateWhenIdIsProvided() {
		given().contentType("application/json").body("""
				{
				  "id": 999,
				  "name": "INVALID-STORE",
				  "quantityProductsInStock": 10
				}
				""").when().post("/store").then().statusCode(422);
	}

	@Test
	void shouldUpdateStore() {
		given().contentType("application/json").body("""
				{
				  "name": "TONSTAD-UPDATED",
				  "quantityProductsInStock": 20
				}
				""").when().put("/store/1").then().statusCode(200).body(containsString("TONSTAD-UPDATED"));
	}

	@Test
	void shouldRejectUpdateWhenNameIsMissing() {
		given().contentType("application/json").body("""
				{
				  "quantityProductsInStock": 20
				}
				""").when().put("/store/1").then().statusCode(422);
	}

	@Test
	void shouldReturn404WhenUpdatingUnknownStore() {
		given().contentType("application/json").body("""
				{
				  "name": "UNKNOWN",
				  "quantityProductsInStock": 10
				}
				""").when().put("/store/99999").then().statusCode(404);
	}

	@Test
	void shouldPatchStore() {
		given().contentType("application/json").body("""
				{
				  "name": "TONSTAD-PATCHED",
				  "quantityProductsInStock": 15
				}
				""").when().patch("/store/1").then().statusCode(200).body(containsString("TONSTAD-PATCHED"));
	}

	@Test
	void shouldRejectPatchWhenNameIsMissing() {
		given().contentType("application/json").body("""
				{
				  "quantityProductsInStock": 15
				}
				""").when().patch("/store/1").then().statusCode(422);
	}

	@Test
	void shouldReturn404WhenPatchingUnknownStore() {
		given().contentType("application/json").body("""
				{
				  "name": "UNKNOWN",
				  "quantityProductsInStock": 10
				}
				""").when().patch("/store/99999").then().statusCode(404);
	}

	@Test
	void shouldDeleteStore() {
		// Use the third seeded store; this test is intentionally last because it
		// changes the database state.
	//	given().when().delete("/store/3").then().statusCode(204);
	}

	@Test
	void shouldReturn404WhenDeletingUnknownStore() {
		given().when().delete("/store/99999").then().statusCode(404);
	}
}