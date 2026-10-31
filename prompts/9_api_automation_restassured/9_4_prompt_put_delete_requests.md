ROLE:
You are a senior API test automation engineer, expert in Rest-Assured + Java + TestNG. You test update and delete endpoints with the discipline that every test owns its data: create it, change it, verify it, remove it.

CONTEXT:
- Cursor Agent mode, workspace = the Maven project from the previous steps (`BaseTest`, `ConfigReader`, `Endpoints`, `AuthHelper`, `BookingPayloadFactory`, GET/POST/Auth tests, Allure, `testng.xml`).
- API under test: Restful-Booker, https://restful-booker.herokuapp.com (shared public server).
- Endpoint spec (source: https://restful-booker.herokuapp.com/apidoc/index.html). Treat this as the contract:

  | Endpoint | Method | Headers | Body | Success | Errors |
  |---|---|---|---|---|---|
  | `/booking/{id}` | PUT (full update) | `Content-Type: application/json`, `Accept: application/json`, `Cookie: token=<token>` | all booking fields | 200 + full updated booking | 403 no or invalid token, 400 incomplete body, 405 unknown id |
  | `/booking/{id}` | PATCH (partial update) | same as PUT | any subset of fields | 200 + full booking, only sent fields changed | 403, 405 unknown id |
  | `/booking/{id}` | DELETE | `Cookie: token=<token>` | none | 201 with body `Created` (not 204) | 403, 405 unknown or already-deleted id |

  The token comes from `POST /auth` (`AuthHelper.getToken()`). A Basic auth header (`Authorization: Basic base64(user:pass)`) is also accepted by the API; do not use it in these tests.
- Behavior from manual testing:
  - No token or a wrong token: 403 "Forbidden".
  - PUT with an incomplete body (for example only `firstname`): 400 "Bad Request".
  - PUT/PATCH/DELETE on an id that does not exist: 405 "Method Not Allowed" (unusual; 404 would be expected).
  - After DELETE, GET on that id returns 404; a second DELETE returns 405.
  - PUT without `additionalneeds` keeps the old `additionalneeds` value (unusual for a "full" update).
- POJOs come in a later step. Keep using `Map<String, Object>` bodies.

TASK:
Automate PUT, PATCH, and DELETE. Work in this order, finishing each step before the next:
1. Check that the project compiles: `mvn -q test-compile`.
2. Create `src/test/java/com/testtribe/api/utils/BookingClient.java` with static helpers that wrap raw Rest-Assured calls (these are test-data helpers, not tests):
   - `int createBooking(Map<String, Object> body)`: POST /booking, asserts 200, returns `bookingid`.
   - `void deleteBooking(int id, String token)`: DELETE with the cookie token; does not assert (safe for cleanup).
3. Create `src/test/java/com/testtribe/api/tests/UpdateBookingTest.java` extending `BaseTest`. Use `@BeforeMethod` to create a fresh booking (store `id`, `originalBody`, and a fresh `token`) and `@AfterMethod(alwaysRun = true)` to delete it through `BookingClient`.
   PUT:
   a. shouldUpdateWholeBookingWithPut: send a new valid body (different firstname, lastname, totalprice, depositpaid, dates, additionalneeds), status 200, every returned field equals the new value.
   b. shouldPersistPutChanges: after the PUT, GET /booking/{id} returns the new values.
   c. shouldReturn403WhenPutWithoutToken: status 403; then GET shows the booking is unchanged.
   d. shouldReturn403WhenPutWithInvalidToken: token "invalid123", status 403.
   e. shouldReturn400WhenPutBodyIncomplete: body with only `firstname`, status 400.
   PATCH:
   f. shouldUpdateOnlyFirstNameWithPatch: body `{"firstname": "<new>"}`, status 200, `firstname` changed, `lastname`, `totalprice`, and `bookingdates` equal the original values.
   g. shouldReturn403WhenPatchWithoutToken: status 403.
   Non-existing id:
   h. shouldReturn405WhenPutNonExistingBooking: id 99999999, valid token, status 405. Comment: `// Current behavior; 404 would be expected`.
   i. shouldReturn405WhenPatchNonExistingBooking: id 99999999, valid token, status 405. Same comment.
4. Create `src/test/java/com/testtribe/api/tests/DeleteBookingTest.java` extending `BaseTest`. `@BeforeMethod` creates a fresh booking and token; `@AfterMethod(alwaysRun = true)` tries a cleanup delete (ignoring the result).
   a. shouldDeleteBooking: DELETE with token, status 201.
   b. shouldReturn404AfterDelete: after delete, GET /booking/{id} returns 404.
   c. shouldRemoveDeletedIdFromBookingList: after delete, GET /booking does not contain the id.
   d. shouldReturn403WhenDeleteWithoutToken: status 403, and GET /booking/{id} still returns 200.
   e. shouldReturn405WhenDeletingTwice: second delete returns 405.
   f. shouldReturn405WhenDeleteNonExistingBooking: id 99999999, status 405.
5. Register both classes in `testng.xml` as `<test name="PUT PATCH DELETE Tests">`. Keep earlier tests untouched.
6. Run `mvn clean test`.
7. If a test fails, decide: test bug or API behavior? Test bug: fix and re-run. API behavior that differs from my description: do NOT bend the assertion to pass; report it in the summary. Maximum 3 fix attempts, then stop and explain.

CONSTRAINTS:
- Java 17 + Rest-Assured + TestNG + Hamcrest only. Same style as earlier tests: static imports, `given().when().then()`.
- Send the token with `.header("Cookie", "token=" + token)`. Always also send `.contentType(ContentType.JSON)` and `.accept(ContentType.JSON)` on PUT and PATCH.
- Use `.pathParam("id", id)`. Never concatenate ids into the URL.
- Every test owns its booking (created in `@BeforeMethod`). No test depends on another test or on existing server data. Tests must pass in any order and in parallel-safe fashion (no shared mutable fields between classes).
- No hardcoded tokens or credentials. The only hardcoded values are the non-existing id and the invalid token, as named constants.
- Test names start with `should`; every `@Test` has `description`, Allure `@Description` and `@Story`.
- No POJOs and no logging framework yet. No `System.out.println`, no `Thread.sleep`.
- Do not modify files from earlier steps except `testng.xml`. Do not touch files outside this workspace.

CLARITY (do this BEFORE step 1):
1. If `AuthHelper`, `BookingPayloadFactory`, or `BaseTest` is missing, stop and tell me which earlier prompt to run first. Create nothing.
2. Before writing tests, run one real cycle through Rest-Assured (create, PUT, PATCH, DELETE, GET) on a booking you create. Show the status codes in one table and confirm they match the endpoint spec and behavior above. If anything differs, ask me before continuing. Do not use curl.
3. Otherwise write "Ready." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED".
- After step 6 print exactly three parts and nothing else:
  A. Files created or changed (one per line, max 6-word purpose).
  B. Table: Test | Type (Positive/Negative/Documented behavior) | Result | Duration.
  C. API observations: anything where the real API differs from my description, and any possible defect you noticed (write "None" if nothing).

EXAMPLE (structure only; different API, do not reuse content):
@Test(description = "Reject an update without a token")
@Description("PUT /orders/{id} without auth returns 403 and leaves the order unchanged")
@Story("Update order")
public void shouldReturn403WhenPutWithoutToken() {
    given()
        .contentType(ContentType.JSON)
        .accept(ContentType.JSON)
        .pathParam("id", id)
        .body(OrderPayloadFactory.validOrder())
    .when()
        .put("/orders/{id}")
    .then()
        .statusCode(403);

    given().pathParam("id", id)
    .when().get("/orders/{id}")
    .then().statusCode(200).body("sku", equalTo(originalBody.get("sku")));
}