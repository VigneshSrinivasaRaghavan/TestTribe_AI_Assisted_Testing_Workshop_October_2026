package com.testtribe.api.tests;

import static com.testtribe.api.utils.ApiConstants.ACCEPT_JSON;
import static com.testtribe.api.utils.Endpoints.BOOKING_BY_ID;
import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

import com.testtribe.api.base.BaseTest;
import com.testtribe.api.pojo.Booking;
import com.testtribe.api.pojo.BookingDates;
import com.testtribe.api.utils.AuthHelper;
import com.testtribe.api.utils.BookingClient;
import com.testtribe.api.utils.BookingPayloadFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class UpdateBookingTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(UpdateBookingTest.class);

    private static final int NON_EXISTING_ID = 99999999;
    private static final String INVALID_TOKEN = "invalid123";

    private int id;
    private Booking originalBody;
    private String token;

    @BeforeMethod
    public void createBooking() {
        originalBody = BookingPayloadFactory.validBooking();
        token = AuthHelper.getToken();
        id = BookingClient.createBooking(originalBody).getBookingid();
    }

    @AfterMethod(alwaysRun = true)
    public void deleteBooking() {
        if (token != null) {
            log.info("Cleaning up booking id={}", id);
            BookingClient.deleteBooking(id, token);
        }
    }

    // ---------- PUT ----------

    @Test(description = "Update the whole booking with PUT")
    @Description("PUT /booking/{id} with a new valid body returns 200 and every returned field equals the new value")
    @Story("Update booking")
    public void shouldUpdateWholeBookingWithPut() {
        Booking newBody = changedBooking();

        Booking updated = jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
            .body(newBody)
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(200)
            .extract().as(Booking.class);

        assertEquals(updated, newBody);
    }

    @Test(description = "PUT changes are persisted")
    @Description("After PUT, GET /booking/{id} returns the new values")
    @Story("Update booking")
    public void shouldPersistPutChanges() {
        Booking newBody = changedBooking();

        jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
            .body(newBody)
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(200);

        assertEquals(getBookingById().then().statusCode(200).extract().as(Booking.class), newBody);
    }

    @Test(description = "Reject PUT without token")
    @Description("PUT /booking/{id} without a token returns 403 and leaves the booking unchanged")
    @Story("Update booking")
    public void shouldReturn403WhenPutWithoutToken() {
        jsonRequest()
            .pathParam("id", id)
            .body(changedBooking())
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(403);

        assertEquals(getBookingById().then().statusCode(200).extract().as(Booking.class), originalBody);
    }

    @Test(description = "Reject PUT with invalid token")
    @Description("PUT /booking/{id} with an invalid token returns 403")
    @Story("Update booking")
    public void shouldReturn403WhenPutWithInvalidToken() {
        jsonRequest()
            .header("Cookie", "token=" + INVALID_TOKEN)
            .pathParam("id", id)
            .body(changedBooking())
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(403);
    }

    @Test(description = "Reject PUT with incomplete body")
    @Description("PUT /booking/{id} with only firstname returns 400")
    @Story("Update booking")
    public void shouldReturn400WhenPutBodyIncomplete() {
        jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
            .body(Booking.builder().firstname("OnlyFirstName").build())
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(400);
    }

    // ---------- PATCH ----------

    @Test(description = "Update only first name with PATCH")
    @Description("PATCH /booking/{id} with firstname changes only firstname; other fields keep original values")
    @Story("Update booking")
    public void shouldUpdateOnlyFirstNameWithPatch() {
        String newFirstName = "Patched" + originalBody.getFirstname();

        Booking patched = jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
            .body(Booking.builder().firstname(newFirstName).build())
        .when()
            .patch(BOOKING_BY_ID)
        .then()
            .statusCode(200)
            .extract().as(Booking.class);

        assertEquals(patched.getFirstname(), newFirstName);
        assertEquals(patched.getLastname(), originalBody.getLastname());
        assertEquals(patched.getTotalprice(), originalBody.getTotalprice());
        assertEquals(patched.getBookingdates(), originalBody.getBookingdates());
    }

    @Test(description = "Reject PATCH without token")
    @Description("PATCH /booking/{id} without a token returns 403")
    @Story("Update booking")
    public void shouldReturn403WhenPatchWithoutToken() {
        jsonRequest()
            .pathParam("id", id)
            .body(Booking.builder().firstname("NoTokenPatch").build())
        .when()
            .patch(BOOKING_BY_ID)
        .then()
            .statusCode(403);
    }

    // ---------- Non-existing id ----------

    @Test(description = "PUT on a non-existing booking")
    @Description("PUT /booking/99999999 with a valid token returns 405 (documented behavior)")
    @Story("Update booking")
    public void shouldReturn405WhenPutNonExistingBooking() {
        // Current behavior; 404 would be expected
        jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", NON_EXISTING_ID)
            .body(changedBooking())
        .when()
            .put(BOOKING_BY_ID)
        .then()
            .statusCode(405);
    }

    @Test(description = "PATCH on a non-existing booking")
    @Description("PATCH /booking/99999999 with a valid token returns 405 (documented behavior)")
    @Story("Update booking")
    public void shouldReturn405WhenPatchNonExistingBooking() {
        // Current behavior; 404 would be expected
        jsonRequest()
            .header("Cookie", "token=" + token)
            .pathParam("id", NON_EXISTING_ID)
            .body(Booking.builder().firstname("Ghost").build())
        .when()
            .patch(BOOKING_BY_ID)
        .then()
            .statusCode(405);
    }

    // ---------- Helpers ----------

    private RequestSpecification jsonRequest() {
        return given()
            .contentType(ContentType.JSON)
            .accept(ACCEPT_JSON);
    }

    private Response getBookingById() {
        return given()
            .accept(ACCEPT_JSON)
            .pathParam("id", id)
        .when()
            .get(BOOKING_BY_ID);
    }

    /** A valid body where every field differs from the original booking. */
    private Booking changedBooking() {
        return Booking.builder()
            .firstname("New" + originalBody.getFirstname())
            .lastname("New" + originalBody.getLastname())
            .totalprice(originalBody.getTotalprice() + 1000)
            .depositpaid(!originalBody.getDepositpaid())
            .bookingdates(BookingDates.builder()
                .checkin(LocalDate.now().plusDays(10).toString())
                .checkout(LocalDate.now().plusDays(15).toString())
                .build())
            .additionalneeds("Dinner")
            .build();
    }
}
