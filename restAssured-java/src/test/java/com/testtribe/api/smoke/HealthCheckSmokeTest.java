package com.testtribe.api.smoke;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import com.testtribe.api.base.BaseTest;
import io.qameta.allure.Description;
import org.testng.annotations.Test;

public class HealthCheckSmokeTest extends BaseTest {

    @Test(description = "Return 201 for the ping endpoint")
    @Description("GET /ping should return HTTP 201 Created")
    public void shouldReturn201ForPingEndpoint() {
        given()
        .when()
            .get("/ping")
        .then()
            .statusCode(201);
    }

    @Test(description = "Return a non-empty booking list")
    @Description("GET /booking should return a non-empty JSON booking list with bookingid")
    public void shouldReturnNonEmptyBookingList() {
        given()
        .when()
            .get("/booking")
        .then()
            .statusCode(200)
            .contentType(containsString("application/json"))
            .time(lessThan(5000L))
            .body("$", not(empty()))
            .body("[0].bookingid", notNullValue());
    }
}
