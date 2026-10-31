ROLE:
You are a senior API test automation engineer, expert in Rest-Assured + Java + TestNG. You test POST endpoints by checking the response AND confirming the data was really stored. You clean up the data your tests create.

CONTEXT:
- Cursor Agent mode, workspace = the Maven project from the previous steps (`BaseTest`, `ConfigReader`, `Endpoints`, `GetBookingTest`, Allure, `testng.xml`).
- API under test: Restful-Booker, https://restful-booker.herokuapp.com (shared public server, base URL and credentials are in `config.properties`).
- Endpoint spec (source: https://restful-booker.herokuapp.com/apidoc/index.html). Treat this as the contract:

  | Endpoint | Method | Headers | Body | Success | Response |
  |---|---|---|---|---|---|
  | `/auth` | POST | `Content-Type: application/json` | `{"username", "password"}` | 200 | `{"token": "..."}`, or `{"reason": "Bad credentials"}` (also 200, no token) |
  | `/booking` | POST | `Content-Type: application/json`, `Accept: application/json` | `{"firstname", "lastname", "totalprice", "depositpaid", "bookingdates": {"checkin", "checkout"}, "additionalneeds"}` (`additionalneeds` optional, dates yyyy-MM-dd) | 200 | `{"bookingid": <int>, "booking": { ...same fields as sent... }}` |
  | `/booking/{id}` | DELETE | `Cookie: token=<token>` | none | 201 | `Created` (cleanup only) |

  Example body: `{"firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true, "bookingdates": {"checkin": "2026-11-01", "checkout": "2026-11-05"}, "additionalneeds": "Breakfast"}`
- Known behavior from manual testing: missing `bookingdates` or missing Content-Type returns 500. Weak validation exists (for example a text value in `totalprice` is accepted and stored as null; an empty `firstname` is accepted).
- POJOs come in a later step. For now, build request bodies with `Map<String, Object>`.

TASK:
Automate POST /auth and POST /booking. Work in this order, finishing each step before the next:
1. Check that the project compiles: `mvn -q test-compile`.
2. Create `src/test/java/com/testtribe/api/utils/AuthHelper.java`: static `getToken()` that calls POST /auth with credentials from `ConfigReader`, asserts status 200, returns the token string. Cache nothing; each call is a fresh login.
3. Create `src/test/java/com/testtribe/api/utils/BookingPayloadFactory.java`: static methods `validBooking()` returning a `Map<String, Object>` with random-but-valid data (random first and last name, price 100-999, depositpaid random, checkin = today + 1 day, checkout = today + 5 days, additionalneeds "Breakfast") and `validBookingWithoutAdditionalNeeds()`. Dates via `LocalDate`, format yyyy-MM-dd.
4. Create `src/test/java/com/testtribe/api/tests/AuthTest.java` extending `BaseTest`:
   a. shouldReturnTokenForValidCredentials: status 200, `token` is a non-empty string.
   b. shouldRejectInvalidCredentials: wrong password, status 200, `reason` equals "Bad credentials", no `token` key.
   c. shouldRejectEmptyCredentials: body `{}`, same expectations as (b).
5. Create `src/test/java/com/testtribe/api/tests/CreateBookingTest.java` extending `BaseTest`. Keep a `List<Integer> createdIds`; every test that creates a booking adds its id to it. In `@AfterClass(alwaysRun = true)` delete every created booking with DELETE /booking/{id} and a `Cookie: token=<token>` header (cleanup only; expect status 201; do not fail the class if a delete fails).
   Positive:
   a. shouldCreateBookingWithValidData: status 200, `bookingid` greater than 0, every field in `booking` equals what was sent (including nested dates).
   b. shouldStoreCreatedBooking: after (a)'s style of creation, GET /booking/{bookingid} returns 200 and the same field values (this proves persistence).
   c. shouldCreateBookingWithoutAdditionalNeeds: status 200, `booking.additionalneeds` is absent or null.
   d. shouldReturnUniqueIdsForTwoBookings: create two bookings, ids differ.
   Negative:
   e. shouldReturn500WhenBookingDatesMissing: body without `bookingdates`, status 500.
   f. shouldReturn500WhenContentTypeMissing: valid body, no Content-Type header, status 500.
   Documented weak validation (these pass today, so they pin the CURRENT behavior):
   g. shouldAcceptEmptyFirstName: status 200, `booking.firstname` is empty. Add Allure `@Issue("Weak validation: empty firstname accepted")` and a comment `// Current behavior, arguably a defect`.
   h. shouldStoreNullWhenTotalPriceIsText: `totalprice` = "abc", status 200, `booking.totalprice` is null. Add Allure `@Issue("Weak validation: text totalprice accepted as null")` and the same comment.
6. Register `AuthTest` and `CreateBookingTest` in `testng.xml` as `<test name="POST Tests">`. Keep earlier tests untouched.
7. Run `mvn clean test`.
8. If a test fails, decide: test bug or API behavior? Test bug: fix and re-run. API behavior that differs from my description: do NOT bend the assertion to pass; report it in the summary. Maximum 3 fix attempts, then stop and explain.

CONSTRAINTS:
- Java 17 + Rest-Assured + TestNG + Hamcrest only. Same style as earlier tests: static imports, `given().when().then()`.
- Send JSON with `.contentType(ContentType.JSON)` and `.body(map)`. Never build JSON by string concatenation.
- Each test creates its own data. No test depends on another test or on existing server data.
- No hardcoded names, tokens, or ids. Random data comes from `BookingPayloadFactory`; credentials from `ConfigReader`.
- Test names start with `should`; every `@Test` has `description`, Allure `@Description` and `@Story`.
- No POJOs, no logging framework, no PUT/PATCH tests yet. No `System.out.println`, no `Thread.sleep`.
- Do not modify files from earlier steps except `testng.xml` (and `Endpoints.java`, only to add constants `AUTH` and `BOOKING_BY_ID` if missing). Do not touch files outside this workspace.

CLARITY (do this BEFORE step 1):
1. If `BaseTest`, `ConfigReader`, or `Endpoints` is missing, stop and tell me which earlier prompt to run first. Create nothing.
2. Before writing tests, call POST /auth and POST /booking once each through Rest-Assured, show me the real response in 6 lines max, and confirm it matches the endpoint spec above. If it differs, ask me before continuing. Do not use curl. Delete the booking you created.
3. Otherwise write "Ready." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED".
- After step 7 print exactly three parts and nothing else:
  A. Files created or changed (one per line, max 6-word purpose).
  B. Table: Test | Type (Positive/Negative/Documented behavior) | Result | Duration.
  C. API observations: anything where the real API differs from my description, and any possible defect you noticed (write "None" if nothing).

EXAMPLE (structure only; different API, do not reuse content):
@Test(description = "Create an order and confirm it is stored")
@Description("POST /orders returns the new id and GET /orders/{id} returns the same data")
@Story("Create order")
public void shouldStoreCreatedOrder() {
    Map<String, Object> order = OrderPayloadFactory.validOrder();

    int id = given()
        .contentType(ContentType.JSON)
        .body(order)
    .when()
        .post("/orders")
    .then()
        .statusCode(201)
        .body("sku", equalTo(order.get("sku")))
        .extract().path("id");
    createdIds.add(id);

    given().pathParam("id", id)
    .when().get("/orders/{id}")
    .then().statusCode(200).body("sku", equalTo(order.get("sku")));
}