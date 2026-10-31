package com.testtribe.api.utils;

import static com.testtribe.api.utils.Endpoints.AUTH;
import static io.restassured.RestAssured.given;

import com.testtribe.api.pojo.AuthRequest;
import com.testtribe.api.pojo.AuthResponse;
import io.restassured.http.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AuthHelper {

    private static final Logger log = LogManager.getLogger(AuthHelper.class);

    private AuthHelper() {
    }

    /** Fresh login on every call; nothing is cached. */
    public static String getToken() {
        log.info("Requesting auth token");
        AuthRequest credentials = AuthRequest.builder()
                .username(ConfigReader.get("auth.username"))
                .password(ConfigReader.get("auth.password"))
                .build();

        return login(credentials).getToken();
    }

    public static AuthResponse login(AuthRequest request) {
        return given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post(AUTH)
        .then()
            .statusCode(200)
            .extract().as(AuthResponse.class);
    }
}
