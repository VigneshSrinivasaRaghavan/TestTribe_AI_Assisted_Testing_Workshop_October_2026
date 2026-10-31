package com.testtribe.api.pojo;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.Test;

public class PojoSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test(description = "Serialize a Booking to the expected JSON")
    @Description("A Booking built with the builder serializes to exactly the 6 expected keys")
    @Story("POJO mapping")
    public void shouldSerializeBookingToExpectedJson() throws JsonProcessingException {
        Booking booking = Booking.builder()
                .firstname("Jim")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(BookingDates.builder().checkin("2026-11-01").checkout("2026-11-05").build())
                .additionalneeds("Breakfast")
                .build();

        JsonNode json = mapper.readTree(mapper.writeValueAsString(booking));

        List<String> keys = new ArrayList<>();
        json.fieldNames().forEachRemaining(keys::add);
        assertEquals(keys.size(), 6);
        assertEquals(json.get("firstname").asText(), "Jim");
        assertEquals(json.get("lastname").asText(), "Brown");
        assertEquals(json.get("totalprice").asInt(), 111);
        assertEquals(json.get("depositpaid").asBoolean(), true);
        assertEquals(json.get("bookingdates").get("checkin").asText(), "2026-11-01");
        assertEquals(json.get("bookingdates").get("checkout").asText(), "2026-11-05");
        assertEquals(json.get("additionalneeds").asText(), "Breakfast");
    }

    @Test(description = "Deserialize a POST /booking response")
    @Description("A sample POST /booking response maps to BookingResponse with all fields")
    @Story("POJO mapping")
    public void shouldDeserializeBookingResponse() throws JsonProcessingException {
        String json = "{\"bookingid\":42,\"booking\":{\"firstname\":\"Jim\",\"lastname\":\"Brown\","
                + "\"totalprice\":111,\"depositpaid\":true,\"bookingdates\":{\"checkin\":\"2026-11-01\","
                + "\"checkout\":\"2026-11-05\"},\"additionalneeds\":\"Breakfast\"}}";

        BookingResponse response = mapper.readValue(json, BookingResponse.class);

        assertEquals(response.getBookingid(), Integer.valueOf(42));
        assertEquals(response.getBooking().getFirstname(), "Jim");
        assertEquals(response.getBooking().getLastname(), "Brown");
        assertEquals(response.getBooking().getTotalprice(), Integer.valueOf(111));
        assertEquals(response.getBooking().getDepositpaid(), Boolean.TRUE);
        assertEquals(response.getBooking().getBookingdates().getCheckin(), "2026-11-01");
        assertEquals(response.getBooking().getBookingdates().getCheckout(), "2026-11-05");
        assertEquals(response.getBooking().getAdditionalneeds(), "Breakfast");
    }

    @Test(description = "Ignore unknown JSON fields")
    @Description("JSON with an extra field deserializes without an exception")
    @Story("POJO mapping")
    public void shouldIgnoreUnknownFields() throws JsonProcessingException {
        Booking booking = mapper.readValue("{\"firstname\":\"Jim\",\"unknown\":\"x\"}", Booking.class);

        assertEquals(booking.getFirstname(), "Jim");
    }

    @Test(description = "Omit null fields on serialize")
    @Description("A Booking with only firstname serializes to {\"firstname\":\"...\"}")
    @Story("POJO mapping")
    public void shouldOmitNullFieldsOnSerialize() throws JsonProcessingException {
        String json = mapper.writeValueAsString(Booking.builder().firstname("Jim").build());

        assertEquals(json, "{\"firstname\":\"Jim\"}");
    }

    @Test(description = "Deserialize an auth failure")
    @Description("{\"reason\":\"Bad credentials\"} gives reason set and token null")
    @Story("POJO mapping")
    public void shouldDeserializeAuthFailure() throws JsonProcessingException {
        AuthResponse response = mapper.readValue("{\"reason\":\"Bad credentials\"}", AuthResponse.class);

        assertEquals(response.getReason(), "Bad credentials");
        assertNull(response.getToken());
    }
}
