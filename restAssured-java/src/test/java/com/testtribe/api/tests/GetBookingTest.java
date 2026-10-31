package com.testtribe.api.tests;

import static com.testtribe.api.utils.ApiConstants.ACCEPT_JSON;
import static com.testtribe.api.utils.Endpoints.BOOKING;
import static com.testtribe.api.utils.Endpoints.BOOKING_BY_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.testtribe.api.base.BaseTest;
import com.testtribe.api.pojo.Booking;
import com.testtribe.api.pojo.BookingId;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.testng.annotations.Test;

public class GetBookingTest extends BaseTest {

    private static final int NON_EXISTING_ID = 99999999;
    private static final String NON_NUMERIC_ID = "abc";
    private static final String NOT_FOUND_BODY = "Not Found";
    private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

    // ---------- Booking list ----------

    @Test(description = "Return all booking ids")
    @Description("GET /booking returns 200 JSON with a non-empty list of positive integer bookingid values")
    @Story("Get booking list")
    public void shouldReturnAllBookingIds() {
        BookingId[] ids = given()
            .accept(ACCEPT_JSON)
        .when()
            .get(BOOKING)
        .then()
            .statusCode(200)
            .contentType(containsString("application/json"))
            .extract().as(BookingId[].class);

        assertThat(ids, not(emptyArray()));
        for (BookingId bookingId : ids) {
            assertThat(bookingId.getBookingid(), allOf(notNullValue(), greaterThan(0)));
        }
    }

    @Test(description = "Filter bookings by first name")
    @Description("GET /booking?firstname=<name> returns 200 and includes the booking the name was read from")
    @Story("Get booking list")
    public void shouldFilterBookingsByFirstName() {
        int id = firstBookingId();
        String firstName = getBookingById(id)
            .then()
            .statusCode(200)
            .extract().as(Booking.class)
            .getFirstname();

        BookingId[] ids = given()
            .accept(ACCEPT_JSON)
            .queryParam("firstname", firstName)
        .when()
            .get(BOOKING)
        .then()
            .statusCode(200)
            .extract().as(BookingId[].class);

        assertThat(idsOf(ids), hasItem(id));
    }

    @Test(description = "Return empty list for unknown first name")
    @Description("GET /booking?firstname=<random UUID> returns 200 with an empty list")
    @Story("Get booking list")
    public void shouldReturnEmptyListForUnknownName() {
        BookingId[] ids = given()
            .accept(ACCEPT_JSON)
            .queryParam("firstname", UUID.randomUUID().toString())
        .when()
            .get(BOOKING)
        .then()
            .statusCode(200)
            .extract().as(BookingId[].class);

        assertThat(ids, emptyArray());
    }

    // ---------- Single booking ----------

    @Test(description = "Return details of an existing booking")
    @Description("GET /booking/{id} returns the booking fields with correct types and date format")
    @Story("Get single booking")
    public void shouldReturnBookingDetails() {
        int id = firstBookingId();

        Booking booking = getBookingById(id)
            .then()
            .statusCode(200)
            .extract().as(Booking.class);

        assertThat(booking.getFirstname(), not(emptyOrNullString()));
        assertThat(booking.getLastname(), not(emptyOrNullString()));
        assertThat(booking.getTotalprice(), notNullValue());
        assertThat(booking.getDepositpaid(), notNullValue());
        assertThat(booking.getBookingdates().getCheckin(), matchesPattern(DATE_PATTERN));
        assertThat(booking.getBookingdates().getCheckout(), matchesPattern(DATE_PATTERN));
    }

    @Test(description = "Checkout date is not before checkin date")
    @Description("GET /booking/{id} returns bookingdates where checkout is on or after checkin")
    @Story("Get single booking")
    public void shouldReturnCheckoutAfterCheckin() {
        int id = firstBookingId();

        Booking booking = getBookingById(id)
            .then()
            .statusCode(200)
            .extract().as(Booking.class);

        LocalDate checkin = LocalDate.parse(booking.getBookingdates().getCheckin());
        LocalDate checkout = LocalDate.parse(booking.getBookingdates().getCheckout());

        assertThat("checkout must not be before checkin", checkout.isBefore(checkin), is(false));
    }

    // ---------- Negative ----------

    @Test(description = "Return 404 for a non-existing booking id")
    @Description("GET /booking/99999999 returns 404 with body 'Not Found'")
    @Story("Get single booking")
    public void shouldReturn404ForNonExistingBooking() {
        getBookingById(NON_EXISTING_ID)
        .then()
            .statusCode(404)
            .body(equalTo(NOT_FOUND_BODY));
    }

    @Test(description = "Return 404 for a non-numeric booking id")
    @Description("GET /booking/abc returns 404 with body 'Not Found'")
    @Story("Get single booking")
    public void shouldReturn404ForNonNumericId() {
        getBookingById(NON_NUMERIC_ID)
        .then()
            .statusCode(404)
            .body(equalTo(NOT_FOUND_BODY));
    }

    // ---------- Non-functional ----------

    @Test(description = "Respond within five seconds")
    @Description("GET /booking/{id} responds in under 5000 ms")
    @Story("Get single booking")
    public void shouldRespondWithinFiveSeconds() {
        int id = firstBookingId();

        getBookingById(id)
        .then()
            .statusCode(200)
            .time(lessThan(5000L));
    }

    // ---------- Helpers ----------

    private int firstBookingId() {
        BookingId[] ids = given()
            .accept(ACCEPT_JSON)
        .when()
            .get(BOOKING)
        .then()
            .statusCode(200)
            .extract().as(BookingId[].class);

        return ids[0].getBookingid();
    }

    private Response getBookingById(Object id) {
        return given()
            .accept(ACCEPT_JSON)
            .pathParam("id", id)
        .when()
            .get(BOOKING_BY_ID);
    }

    private static List<Integer> idsOf(BookingId[] ids) {
        return Arrays.stream(ids).map(BookingId::getBookingid).toList();
    }
}
