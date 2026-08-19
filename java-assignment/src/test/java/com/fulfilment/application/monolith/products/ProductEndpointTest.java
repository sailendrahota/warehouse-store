package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

	@Test
	public void shouldListProducts() {
		given().when().get("/product").then().statusCode(200).body(containsString("TONSTAD"), containsString("KALLAX"),
				containsString("TEST-PRODUCT"));
	}

	@Test
	public void shouldGetProductById() {
		given().when().get("/product/1").then().statusCode(200).body(containsString("TONSTAD"));
	}

	@Test
	public void shouldReturn404WhenProductDoesNotExist() {
		given().when().get("/product/99999").then().statusCode(404)
				.body(containsString("Product with id of 99999 does not exist."));
	}

	@Test
	public void shouldCreateProduct() {
		given().contentType("application/json").body("""
				{
				  "name": "TEST-PRODUCT",
				  "description": "Coverage test product",
				  "price": 10.50,
				  "stock": 5
				}
				""").when().post("/product").then().statusCode(201).body(containsString("TEST-PRODUCT"));
	}

	@Test
	public void shouldRejectCreateWhenIdIsProvided() {
		given().contentType("application/json").body("""
				{
				  "id": 999,
				  "name": "INVALID-PRODUCT",
				  "description": "Invalid",
				  "price": 10.50,
				  "stock": 5
				}
				""").when().post("/product").then().statusCode(422)
				.body(containsString("Id was invalidly set on request."));
	}

	@Test
	public void shouldUpdateProduct() {
		given().contentType("application/json").body("""
				{
				  "name": "TONSTAD-UPDATED",
				  "description": "Updated product",
				  "price": 25.50,
				  "stock": 20
				}
				""").when().put("/product/1").then().statusCode(200).body(containsString("TONSTAD-UPDATED"));
	}

	@Test
	public void shouldRejectUpdateWhenNameIsMissing() {
		given().contentType("application/json").body("""
				{
				  "description": "Updated product",
				  "price": 25.50,
				  "stock": 20
				}
				""").when().put("/product/1").then().statusCode(422)
				.body(containsString("Product Name was not set on request."));
	}

	@Test
	public void shouldReturn404WhenUpdatingUnknownProduct() {
		given().contentType("application/json").body("""
				{
				  "name": "UNKNOWN",
				  "description": "Unknown product",
				  "price": 10.00,
				  "stock": 5
				}
				""").when().put("/product/99999").then().statusCode(404)
				.body(containsString("Product with id of 99999 does not exist."));
	}

	@Test
	public void shouldDeleteProduct() {
		given().when().delete("/product/3").then().statusCode(204);
	}

	@Test
	public void shouldReturn404WhenDeletingUnknownProduct() {
		given().when().delete("/product/99999").then().statusCode(404)
				.body(containsString("Product with id of 99999 does not exist."));
	}
}