package com.testtribe.api.utils;

public final class ApiConstants {

    /**
     * Exact Accept value from the API contract. RestAssured's ContentType.JSON sends a multi-value
     * Accept header ("application/json, application/javascript, text/javascript, text/json"),
     * which the server answers with 418 on /booking endpoints.
     */
    public static final String ACCEPT_JSON = "application/json";

    private ApiConstants() {
    }
}
