package com.testtribe.api.tests;

import static com.testtribe.api.utils.ApiConstants.ACCEPT_JSON;
import static com.testtribe.api.utils.Endpoints.BOOKING;
import static com.testtribe.api.utils.Endpoints.BOOKING_BY_ID;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testtribe.api.base.BaseTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.testtribe.api.pojo.Booking;
import com.testtribe.api.pojo.BookingResponse;
import com.testtribe.api.utils.AuthHelper;
import com.testtribe.api.utils.BookingClient;
import com.testtribe.api.utils.BookingPayloadFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

public class CreateBookingTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(CreateBookingTest.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final List<Integer> createdIds = Collections.synchronizedList(new ArrayList<>());

    @AfterClass(alwaysRun = true)
    public void deleteCreatedBookings() {
        if (createdIds.isEmpty()) {
            return;
        }
        try {
            log.info("Cleaning up {} created bookings", createdIds.size());
            String token = AuthHelper.getToken();
            for (int id : new ArrayList<>(createdIds)) {
                BookingClient.deleteBooking(id, token);
            }
        } catch (RuntimeException | AssertionError e) {
            // Cleanup only: never fail the class because of a failed delete.
            log.warn("Cleanup failed: {}", e.getMessage());
        }
    }

    // ---------- Positive ----------

    @Test(description = "Create a booking with valid data")
    @Description("POST /booking returns 200, a positive bookingid and echoes every field that was sent")
    @Story("Create booking")
    public void shouldCreateBookingWithValidData() {
        Booking booking = BookingPayloadFactory.validBooking();

        BookingResponse response = postBooking(booking)
            .then()
            .statusCode(200)
            .extract().as(BookingResponse.class);

        assertThat(response.getBookingid(), greaterThan(0));
        assertEquals(response.getBooking(), booking);
    }

    @Test(description = "Created booking is stored")
    @Description("POST /booking then GET /booking/{id} returns 200 and the same field values")
    @Story("Create booking")
    public void shouldStoreCreatedBooking() {
        Booking booking = BookingPayloadFactory.validBooking();
        int id = postBooking(booking)
            .then()
            .statusCode(200)
            .extract().as(BookingResponse.class)
            .getBookingid();

        Booking stored = given()
            .accept(ACCEPT_JSON)
            .pathParam("id", id)
        .when()
            .get(BOOKING_BY_ID)
        .then()
            .statusCode(200)
            .extract().as(Booking.class);

        assertEquals(stored, booking);
    }

    @Test(description = "Create a booking without additional needs")
    @Description("POST /booking without additionalneeds returns 200 and booking.additionalneeds is absent or null")
    @Story("Create booking")
    public void shouldCreateBookingWithoutAdditionalNeeds() {
        BookingResponse response = postBooking(BookingPayloadFactory.validBookingWithoutAdditionalNeeds())
            .then()
            .statusCode(200)
            .extract().as(BookingResponse.class);

        assertNull(response.getBooking().getAdditionalneeds());
    }

    @Test(description = "Return unique ids for two bookings")
    @Description("Two POST /booking calls return different bookingid values")
    @Story("Create booking")
    public void shouldReturnUniqueIdsForTwoBookings() {
        int first = postBooking(BookingPayloadFactory.validBooking())
            .then().statusCode(200).extract().as(BookingResponse.class).getBookingid();
        int second = postBooking(BookingPayloadFactory.validBooking())
            .then().statusCode(200).extract().as(BookingResponse.class).getBookingid();

        assertThat("booking ids must be unique", first, is(not(second)));
    }

    // ---------- Negative ----------

    @Test(description = "Return 500 when bookingdates is missing")
    @Description("POST /booking without bookingdates returns 500")
    @Story("Create booking")
    public void shouldReturn500WhenBookingDatesMissing() {
        // Raw body on purpose: invalid shape
        Map<String, Object> booking = asMap(BookingPayloadFactory.validBooking());
        booking.remove("bookingdates");

        postBooking(booking)
        .then()
            .statusCode(500);
    }

    @Test(description = "Return 500 when Content-Type is missing")
    @Description("POST /booking with a valid body but no Content-Type header returns 500")
    @Story("Create booking")
    public void shouldReturn500WhenContentTypeMissing() throws JsonProcessingException {
        String json = MAPPER.writeValueAsString(BookingPayloadFactory.validBooking());

        given()
            .accept(ACCEPT_JSON)
            .body(json.getBytes(StandardCharsets.UTF_8))
        .when()
            .post(BOOKING)
        .then()
            .statusCode(500);
    }

    // ---------- Documented weak validation (pins CURRENT behavior) ----------

    @Test(description = "Accept an empty first name")
    @Description("POST /booking with firstname '' returns 200 and stores the empty value")
    @Story("Create booking")
    @Issue("Weak validation: empty firstname accepted")
    public void shouldAcceptEmptyFirstName() {
        Booking booking = BookingPayloadFactory.validBooking();
        booking.setFirstname("");

        // Current behavior, arguably a defect
        BookingResponse response = postBooking(booking)
            .then()
            .statusCode(200)
            .extract().as(BookingResponse.class);

        assertEquals(response.getBooking().getFirstname(), "");
    }

    @Test(description = "Store null when totalprice is text")
    @Description("POST /booking with totalprice 'abc' returns 200 and booking.totalprice is null")
    @Story("Create booking")
    @Issue("Weak validation: text totalprice accepted as null")
    public void shouldStoreNullWhenTotalPriceIsText() {
        // Raw body on purpose: invalid shape
        Map<String, Object> booking = asMap(BookingPayloadFactory.validBooking());
        booking.put("totalprice", "abc");

        // Current behavior, arguably a defect
        BookingResponse response = postBooking(booking)
            .then()
            .statusCode(200)
            .extract().as(BookingResponse.class);

        assertNull(response.getBooking().getTotalprice());
    }

    // ---------- Helpers ----------

    /** Posts the body (POJO or raw map) and registers the id for cleanup before any assertion runs. */
    private Response postBooking(Object booking) {
        Response response = given()
            .contentType(ContentType.JSON)
            .accept(ACCEPT_JSON)
            .body(booking)
        .when()
            .post(BOOKING);

        if (response.statusCode() == 200) {
            Integer id = response.path("bookingid");
            if (id != null) {
                createdIds.add(id);
            }
        }
        return response;
    }

    private static Map<String, Object> asMap(Booking booking) {
        return MAPPER.convertValue(booking, new TypeReference<Map<String, Object>>() { });
    }
}
