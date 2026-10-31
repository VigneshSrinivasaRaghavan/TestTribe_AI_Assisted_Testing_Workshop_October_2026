package com.testtribe.api.utils;

import static com.testtribe.api.utils.ApiConstants.ACCEPT_JSON;
import static com.testtribe.api.utils.Endpoints.BOOKING;
import static com.testtribe.api.utils.Endpoints.BOOKING_BY_ID;
import static io.restassured.RestAssured.given;

import com.testtribe.api.pojo.Booking;
import com.testtribe.api.pojo.BookingResponse;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Test-data helpers wrapping raw Rest-Assured calls. */
public final class BookingClient {

    private static final Logger log = LogManager.getLogger(BookingClient.class);

    private BookingClient() {
    }

    public static BookingResponse createBooking(Booking body) {
        BookingResponse created = given()
            .contentType(ContentType.JSON)
            .accept(ACCEPT_JSON)
            .body(body)
        .when()
            .post(BOOKING)
        .then()
            .statusCode(200)
            .extract().as(BookingResponse.class);
        log.info("Created booking id={}", created.getBookingid());
        return created;
    }

    /** Cleanup only: does not assert, safe to call for already-deleted ids. */
    public static void deleteBooking(int id, String token) {
        given()
            .header("Cookie", "token=" + token)
            .pathParam("id", id)
        .when()
            .delete(BOOKING_BY_ID);
        log.info("Deleted booking id={}", id);
    }
}
