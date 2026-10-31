package com.testtribe.api.tests;

import static com.testtribe.api.utils.Endpoints.AUTH;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import com.testtribe.api.base.BaseTest;
import com.testtribe.api.pojo.AuthRequest;
import com.testtribe.api.pojo.AuthResponse;
import com.testtribe.api.utils.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import java.util.Map;
import java.util.UUID;
import org.testng.annotations.Test;

public class AuthTest extends BaseTest {

    private static final String BAD_CREDENTIALS = "Bad credentials";

    @Test(description = "Return a token for valid credentials")
    @Description("POST /auth with valid credentials returns 200 and a non-empty token")
    @Story("Authentication")
    public void shouldReturnTokenForValidCredentials() {
        AuthRequest credentials = AuthRequest.builder()
                .username(ConfigReader.get("auth.username"))
                .password(ConfigReader.get("auth.password"))
                .build();

        AuthResponse response = given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post(AUTH)
        .then()
            .statusCode(200)
            .extract().as(AuthResponse.class);

        assertThat(response.getToken(), not(emptyOrNullString()));
    }

    @Test(description = "Reject invalid credentials")
    @Description("POST /auth with a wrong password returns 200, reason 'Bad credentials' and no token")
    @Story("Authentication")
    public void shouldRejectInvalidCredentials() {
        AuthRequest credentials = AuthRequest.builder()
                .username(ConfigReader.get("auth.username"))
                .password(UUID.randomUUID().toString())
                .build();

        AuthResponse response = given()
            .contentType(ContentType.JSON)
            .body(credentials)
        .when()
            .post(AUTH)
        .then()
            .statusCode(200)
            .body("$", not(hasKey("token")))
            .extract().as(AuthResponse.class);

        assertEquals(response.getReason(), BAD_CREDENTIALS);
        assertNull(response.getToken());
    }

    @Test(description = "Reject empty credentials")
    @Description("POST /auth with body {} returns 200, reason 'Bad credentials' and no token")
    @Story("Authentication")
    public void shouldRejectEmptyCredentials() {
        // Raw body on purpose: invalid shape
        AuthResponse response = given()
            .contentType(ContentType.JSON)
            .body(Map.of())
        .when()
            .post(AUTH)
        .then()
            .statusCode(200)
            .body("$", not(hasKey("token")))
            .extract().as(AuthResponse.class);

        assertEquals(response.getReason(), BAD_CREDENTIALS);
        assertNull(response.getToken());
    }
}
