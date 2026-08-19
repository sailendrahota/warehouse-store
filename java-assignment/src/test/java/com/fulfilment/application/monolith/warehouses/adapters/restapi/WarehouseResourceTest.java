package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class WarehouseResourceTest {

	@Test
	void shouldListActiveWarehouses() {
		given().when().get("/warehouse").then().statusCode(200).body(containsString("MWH.001"),
				containsString("MWH.012"), containsString("MWH.023"));
	}

	@Test
	void shouldGetWarehouseById() {
		given().when().get("/warehouse/1").then().statusCode(200).body(containsString("MWH.001"),
				containsString("ZWOLLE-001"));
	}

	@Test
	void shouldReturn404ForUnknownWarehouse() {
		given().when().get("/warehouse/99999").then().statusCode(404);
	}

	@Test
	void shouldCreateWarehouse() {
		given().contentType("application/json").body("""
				{
				  "businessUnitCode": "MWH.TEST.101",
				  "location": "AMSTERDAM-001",
				  "capacity": 20,
				  "stock": 5
				}
				""").when().post("/warehouse").then().statusCode(200).body(containsString("MWH.TEST.101"),
				containsString("AMSTERDAM-001"));
	}

	@Test
	void shouldRejectInvalidLocation() {
		given().contentType("application/json").body("""
				{
				  "businessUnitCode": "MWH.TEST.BAD",
				  "location": "UNKNOWN",
				  "capacity": 10,
				  "stock": 5
				}
				""").when().post("/warehouse").then().statusCode(404);
	}

	@Test
	void shouldArchiveWarehouse() {
		given().when().delete("/warehouse/3").then().statusCode(204);

		given().when().get("/warehouse").then().statusCode(200).body(not(containsString("TILBURG-001")),
				containsString("ZWOLLE-001"), containsString("AMSTERDAM-001"));
	}
}