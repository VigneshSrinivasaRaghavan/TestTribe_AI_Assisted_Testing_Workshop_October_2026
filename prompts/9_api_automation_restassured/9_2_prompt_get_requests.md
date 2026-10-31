ROLE:
You are a senior API test automation engineer, expert in Rest-Assured + Java + TestNG. You write GET tests that are independent, readable, and do not break when public test data changes.

CONTEXT:
- Cursor Agent mode, workspace = the Maven project from the setup step (package `com.testtribe.api`, `BaseTest`, `ConfigReader`, `config.properties`, Allure, TestNG suite in `src/test/resources/testng.xml`).
- API under test: Restful-Booker, https://restful-booker.herokuapp.com (shared public server: other people create, change, and delete bookings at any time).
- Endpoint spec (source: https://restful-booker.herokuapp.com/apidoc/index.html#api-Booking-GetBookings). Treat this as the contract:

  | Endpoint | Method | Params | Header | Success | Response |
  |---|---|---|---|---|---|
  | `/booking` | GET | Query, all optional: `firstname` (string), `lastname` (string), `checkin` (yyyy-MM-dd), `checkout` (yyyy-MM-dd) | `Accept: application/json` | 200 | `[{"bookingid": 1}, ...]` |
  | `/booking/{id}` | GET | Path: `id` (integer) | `Accept: application/json` | 200 | `{"firstname", "lastname", "totalprice", "depositpaid", "bookingdates": {"checkin", "checkout"}, "additionalneeds"}` |

  Errors: unknown or non-numeric id (e.g. 99999999, "abc") returns 404 with body `Not Found`. A filter that matches nothing returns 200 with `[]`.
- Next steps in the course: POJOs, Log4j2, POST/PUT/DELETE. Do not build them now.

TASK:
Automate the GET requests. Work in this order, finishing each step before the next:
1. Check that the setup exists: `pom.xml`, `BaseTest`, `ConfigReader`, `testng.xml`. Run `mvn -q test-compile`. 
2. Send `GET /booking` and one `GET /booking/{id}` (id taken from that list) through Rest-Assured with `Accept: application/json`. Compare the real response with the endpoint spec above in 5 lines max. If it differs, stop and ask me. Do not use curl.
3. Create `src/test/java/com/testtribe/api/utils/Endpoints.java`: `final` class, private constructor, constants `PING`, `BOOKING`, `BOOKING_BY_ID` (= "/booking/{id}").
4. Create `src/test/java/com/testtribe/api/tests/GetBookingTest.java` extending `BaseTest`. Tests (each independent; no test relies on another):
   Booking list:
   a. shouldReturnAllBookingIds: status 200, JSON content type, list not empty, every `bookingid` is an integer greater than 0.
   b. shouldFilterBookingsByFirstName: first read any existing booking's `firstname` (GET list, then GET by id), then GET /booking?firstname=<that name>, status 200, the list contains that booking's id.
   c. shouldReturnEmptyListForUnknownName: firstname = a random UUID, status 200, list is empty.
   Single booking:
   d. shouldReturnBookingDetails: take the first id from the list, GET /booking/{id}, status 200, `firstname` and `lastname` are non-empty strings, `totalprice` is a number, `depositpaid` is a boolean, `bookingdates.checkin` and `bookingdates.checkout` match `\d{4}-\d{2}-\d{2}`.
   e. shouldReturnCheckoutAfterCheckin: same booking, parse both dates with `LocalDate` and assert checkout is not before checkin.
   Negative:
   f. shouldReturn404ForNonExistingBooking: id 99999999, status 404, body equals "Not Found".
   g. shouldReturn404ForNonNumericId: id "abc", status 404, body equals "Not Found".
   Non-functional:
   h. shouldRespondWithinFiveSeconds: GET /booking/{id} with `.time(lessThan(5000L))`.
5. Register the new class in `testng.xml` as a second `<test name="GET Tests">`, keeping Smoke untouched.
6. Run `mvn clean test`.
7. If a test fails, decide: is it a test bug or an API behavior? Test bug: fix and re-run. API behavior that differs from my description above: do NOT bend the assertion to pass; report it to me in the summary. Maximum 3 fix attempts, then stop and explain.

CONSTRAINTS:
- Java 17 + Rest-Assured + TestNG + Hamcrest only. Keep the style of the setup step: static imports, `given().when().then()`.
- Never hardcode a booking id or a name that exists on the server (shared data changes). Always read test data from the live list first. Only the "non-existing" ids (99999999, "abc") are hardcoded, as named constants.
- Use `.accept(ContentType.JSON)`, `.pathParam("id", id)` and `.queryParam(...)`. Never concatenate values into the URL string.
- Extract response values with `.extract().path(...)` or `JsonPath`. Do not create POJOs yet.
- One logical check group per test; test method names start with `should`; each `@Test` has `description` plus Allure `@Description` and `@Story`.
- No `System.out.println`, no `Thread.sleep`. No logging framework yet. Do not modify the setup files except `testng.xml`. Do not touch files outside this workspace.

CLARITY (do this BEFORE step 1):
1. If `BaseTest` or `ConfigReader` is missing, stop and tell me to run the setup prompt first. Create nothing.
2. If the live API does not respond with HTTP 200 on GET /booking within 30 seconds (try twice, the server may be cold), stop and tell me. Do not mock it.
3. If anything in my test list is ambiguous or conflicts with the real response, ask me one precise question. Otherwise write "Ready." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED".
- After step 6 print exactly three parts and nothing else:
  A. Files created or changed (one per line, max 6-word purpose).
  B. Table: Test | Type (Positive/Negative/Non-functional) | Result | Duration.
  C. API observations: anything where the real API differs from my description (write "None" if nothing).

EXAMPLE (structure only; different API, do not reuse content):
@Test(description = "Return details of an existing user")
@Description("GET /users/{id} returns the profile fields with correct types")
@Story("Get user")
public void shouldReturnUserDetails() {
    int id = given().when().get("/users").then().extract().path("[0].id");

    given()
        .pathParam("id", id)
    .when()
        .get("/users/{id}")
    .then()
        .statusCode(200)
        .body("email", matchesPattern(".+@.+"))
        .body("active", instanceOf(Boolean.class));
}