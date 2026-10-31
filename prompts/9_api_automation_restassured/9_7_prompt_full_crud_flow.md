ROLE:
You are a senior API test automation engineer, expert in Rest-Assured + Java + TestNG + Allure. You design end-to-end API flows that tell one clear business story, fail with a clear reason, and never leave data behind.

CONTEXT:
- Cursor Agent mode, workspace = the Maven project from the previous steps (POJOs `Booking`, `BookingDates`, `BookingResponse`, `BookingId`, `AuthRequest`, `AuthResponse`; helpers `AuthHelper`, `BookingClient`, `BookingPayloadFactory`, `Endpoints`, `ConfigReader`, `LogMasker`, `ApiLoggingFilter`, `TestListener`; Log4j2; Allure; `testng.xml`).
- API under test: Restful-Booker, https://restful-booker.herokuapp.com (shared public server).
- Endpoint spec (source: https://restful-booker.herokuapp.com/apidoc/index.html). Treat this as the contract:

  | Flow step | Call | Headers | Expected status | Response |
  |---|---|---|---|---|
  | 1 | `POST /auth` `{"username","password"}` | `Content-Type: application/json` | 200 | `{"token"}` |
  | 2 | `POST /booking` full booking | `Content-Type`, `Accept: application/json` | 200 | `{"bookingid","booking"}` |
  | 3 | `GET /booking/{id}` | `Accept: application/json` | 200 | the booking |
  | 4 | `GET /booking?firstname=..&lastname=..` | none | 200 | `[{"bookingid"}]` (both filters must match) |
  | 5 | `PUT /booking/{id}` full booking | `Content-Type`, `Accept`, `Cookie: token=<token>` | 200 | full booking |
  | 6 | `GET /booking/{id}` | `Accept` | 200 | updated booking |
  | 7 | `PATCH /booking/{id}` `{"firstname"}` | `Content-Type`, `Accept`, `Cookie` | 200 | full booking, only `firstname` changed |
  | 8 | `DELETE /booking/{id}` | `Cookie: token=<token>` | 201 | `Created` |
  | 9 | `GET /booking/{id}` | `Accept` | 404 | `Not Found` |
  | 10 | `GET /booking` | none | 200 | list without the id |
- The earlier test classes test one operation each, with isolated data. This step adds ONE scenario that walks through the whole life of a booking.

TASK:
Create the end-to-end CRUD flow. Work in this order, finishing each step before the next:
1. Run `mvn clean test`. Record the pass count as the BASELINE. If any test fails, stop and tell me.
2. Create `src/test/java/com/testtribe/api/tests/BookingCrudFlowTest.java` extending `BaseTest`, annotated with Allure `@Epic("Booking API")` and `@Feature("CRUD flow")`.
   Shared state (private fields in this class only): `token`, `bookingId`, `createdBooking`, `updatedBooking`. 
   Steps, in this exact order, chained with `priority` AND `dependsOnMethods` so a failed step makes the next ones SKIP instead of failing with a confusing error:
   1. step1_shouldAuthenticate: get a token through `AuthHelper`; assert it is non-empty; store it.
   2. step2_shouldCreateBooking: POST a valid `Booking` (from `BookingPayloadFactory`); assert 200, `bookingid` > 0, response booking equals the sent booking; store `bookingId` and `createdBooking`.
   3. step3_shouldReadCreatedBooking: GET /booking/{id}; assert 200 and the booking equals `createdBooking`.
   4. step4_shouldFindBookingInList: GET /booking?firstname=<createdBooking firstname>&lastname=<lastname>; assert the list contains `bookingId`.
   5. step5_shouldUpdateBookingWithPut: PUT a new full `Booking` (all fields different); assert 200 and the response equals the new booking; store `updatedBooking`.
   6. step6_shouldReadUpdatedBooking: GET; assert it equals `updatedBooking` and differs from `createdBooking` in `firstname`, `lastname`, and `totalprice`.
   7. step7_shouldPatchFirstName: PATCH `{firstname: "<new unique name>"}`; assert 200, `firstname` changed, every other field still equals `updatedBooking`.
   8. step8_shouldDeleteBooking: DELETE; assert status 201.
   9. step9_shouldReturn404AfterDelete: GET /booking/{id}; assert 404.
   10. step10_shouldNotListDeletedBooking: GET /booking; assert it does not contain `bookingId`.
   Guaranteed cleanup: `@AfterClass(alwaysRun = true)` deletes `bookingId` with the token if it was created and the flow did not reach the delete step. Log what it did. It must never throw.
3. Annotate each step method with Allure `@Description` and `@Story("<n>. <short action>")` so the Allure report shows the flow in order.
4. In `testng.xml` add a separate `<test name="E2E CRUD Flow" preserve-order="true">` with this class only. Set `parallel` OFF for this test (the flow shares state and must run in one thread).
5. Run only this class: `mvn test -Dtest=BookingCrudFlowTest` (if `testng.xml` conflicts with `-Dtest`, use `-Dsurefire.suiteXmlFiles` or an equivalent that works, and tell me which). All 10 steps must pass.
6. PROVE the flow stops safely on failure (a one-time experiment): temporarily change the expected status in step 2 to 999, run the class, show that steps 3 to 10 are SKIPPED (not failed) and that the cleanup ran. Then REVERT the change and run the class again to confirm 10 passed. Show `git diff` or the file lines proving the revert.
7. Run the complete suite: `mvn clean test`. The pass count must be BASELINE + 10.
8. Run `mvn allure:report` and confirm `target/site/allure-maven-plugin/index.html` exists.
9. If something fails and you cannot fix it in 3 attempts, stop and explain.

CONSTRAINTS:
- Java 17 + Rest-Assured + TestNG + Hamcrest + Allure only. Same style as earlier tests: static imports, `given().when().then()`, POJOs for request and response bodies.
- Shared state lives ONLY in this class, in private fields. No static mutable state, no state in `BaseTest`, no state in other classes.
- Each step does exactly one API operation plus its assertions. A step must never silently re-create data that an earlier step should have created.
- Random data comes from `BookingPayloadFactory`. No hardcoded names, ids, tokens, or credentials.
- No `Thread.sleep`, no `System.out.println`. Use the existing Log4j2 setup: do not add log lines inside the steps (the filter and listener already log every call).
- Do not modify existing classes except `testng.xml`. Do not touch files outside this workspace.
- Step 6 is a temporary experiment. The final code must contain the correct expected status. Leaving 999 in the file is a failure of this task.

CLARITY (do this BEFORE step 1):
1. If any POJO, `AuthHelper`, `BookingClient`, or `BookingPayloadFactory` is missing, stop and tell me which earlier prompt to run first. Create nothing.
2. Before writing the class, run the whole flow once through the existing helpers and Rest-Assured (auth, create, get, put, patch, delete, get) on a booking you create, and show the status codes in one table. If any status differs from the endpoint spec, ask me before continuing. Do not use curl.
3. Otherwise write "Ready." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED".
- After step 8 print exactly three parts and nothing else:
  A. Files created or changed (one per line, max 6-word purpose).
  B. Table: Flow step | HTTP call | Expected status | Result | Duration. (10 rows, then BASELINE and final pass count.)
  C. Failure-experiment result in 3 lines: which steps were skipped, whether cleanup ran, and proof that the revert is done.

EXAMPLE (structure only; different API, do not reuse content):
@Test(priority = 1, description = "Create an order")
@Story("1. Create order")
public void step1_shouldCreateOrder() {
    Order sent = OrderPayloadFactory.validOrder();
    OrderResponse res = given().contentType(ContentType.JSON).body(sent)
        .when().post("/orders")
        .then().statusCode(201).extract().as(OrderResponse.class);
    orderId = res.getId();
    createdOrder = sent;
    assertEquals(res.getOrder(), sent);
}

@Test(priority = 2, dependsOnMethods = "step1_shouldCreateOrder", description = "Read the order")
@Story("2. Read order")
public void step2_shouldReadOrder() {
    Order actual = given().pathParam("id", orderId)
        .when().get("/orders/{id}")
        .then().statusCode(200).extract().as(Order.class);
    assertEquals(actual, createdOrder);
}