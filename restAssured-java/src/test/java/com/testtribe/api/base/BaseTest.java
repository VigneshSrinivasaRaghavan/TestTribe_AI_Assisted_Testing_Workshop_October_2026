package com.testtribe.api.base;

import com.testtribe.api.utils.ApiLoggingFilter;
import com.testtribe.api.utils.ConfigReader;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    @BeforeClass
    public void setUpBaseUri() {
        RestAssured.reset();
        RestAssured.baseURI = ConfigReader.get("base.uri");
        RestAssured.filters(new ApiLoggingFilter());
    }
}
