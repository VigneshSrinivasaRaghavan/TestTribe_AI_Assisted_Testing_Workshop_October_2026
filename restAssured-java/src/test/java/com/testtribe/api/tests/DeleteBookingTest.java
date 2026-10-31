package com.testtribe.api.tests;

import static com.testtribe.api.utils.ApiConstants.ACCEPT_JSON;
import static com.testtribe.api.utils.Endpoints.BOOKING;
import static com.testtribe.api.utils.Endpoints.BOOKING_BY_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.testtribe.api.base.BaseTest;
import com.testtribe.api.pojo.BookingId;
import com.testtribe.api.utils.AuthHelper;
import com.testtribe.api.utils.BookingClient;
import com.testtribe.api.utils.BookingPayloadFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class DeleteBookingTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(DeleteBookingTest.class);

    private static final int NON_EXISTING_ID = 99999999;

    private int id;
    private String token;

    @BeforeMethod
    public void createBooking() {
        token = AuthHelper.getToken();
        id = BookingClient.createBooking(BookingPayloadFactory.validBooking()).getBookingid();
    }

    @AfterMethod(alwaysRun = true)
    public void cleanUp() {
        if (token != null) {
            log.info("Cleaning up booking id={}", id);
            BookingClient.deleteBooking(id, token);
        }
    }

    @Test(description = "Delete a booking")
    @Description("DELETE /booking/{id} with a token returns 201 with body 'Created'")
    @Story("Delete booking")
    public void shouldDeleteBooking() {
        given()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
        .when()
            .delete(BOOKING_BY_ID)
        .then()
            .statusCode(201)
            .body(equalTo("Created"));
    }

    @Test(description = "Return 404 after delete")
    @Description("After DELETE, GET /booking/{id} returns 404")
    @Story("Delete booking")
    public void shouldReturn404AfterDelete() {
        deleteWithToken();

        given()
            .accept(ACCEPT_JSON)
            .pathParam("id", id)
        .when()
            .get(BOOKING_BY_ID)
        .then()
            .statusCode(404);
    }

    @Test(description = "Deleted id disappears from the list")
    @Description("After DELETE, GET /booking does not contain the id")
    @Story("Delete booking")
    public void shouldRemoveDeletedIdFromBookingList() {
        deleteWithToken();

        BookingId[] ids = given()
            .accept(ACCEPT_JSON)
        .when()
            .get(BOOKING)
        .then()
            .statusCode(200)
            .extract().as(BookingId[].class);

        assertThat(Arrays.stream(ids).map(BookingId::getBookingid).toList(), not(hasItem(id)));
    }

    @Test(description = "Reject DELETE without token")
    @Description("DELETE /booking/{id} without a token returns 403 and the booking still exists")
    @Story("Delete booking")
    public void shouldReturn403WhenDeleteWithoutToken() {
        given()
            .pathParam("id", id)
        .when()
            .delete(BOOKING_BY_ID)
        .then()
            .statusCode(403);

        given()
            .accept(ACCEPT_JSON)
            .pathParam("id", id)
        .when()
            .get(BOOKING_BY_ID)
        .then()
            .statusCode(200);
    }

    @Test(description = "Deleting twice returns 405")
    @Description("A second DELETE on the same id returns 405 (documented behavior)")
    @Story("Delete booking")
    public void shouldReturn405WhenDeletingTwice() {
        deleteWithToken();

        // Current behavior; 404 would be expected
        given()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
        .when()
            .delete(BOOKING_BY_ID)
        .then()
            .statusCode(405);
    }

    @Test(description = "Delete a non-existing booking")
    @Description("DELETE /booking/99999999 with a valid token returns 405 (documented behavior)")
    @Story("Delete booking")
    public void shouldReturn405WhenDeleteNonExistingBooking() {
        // Current behavior; 404 would be expected
        given()
            .header("Cookie", "token=" + token)
            .pathParam("id", NON_EXISTING_ID)
        .when()
            .delete(BOOKING_BY_ID)
        .then()
            .statusCode(405);
    }

    private void deleteWithToken() {
        given()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
        .when()
            .delete(BOOKING_BY_ID)
        .then()
            .statusCode(201);
    }
}
