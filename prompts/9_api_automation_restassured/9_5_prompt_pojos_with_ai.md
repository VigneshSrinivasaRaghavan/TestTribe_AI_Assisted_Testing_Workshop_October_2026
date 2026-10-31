ROLE:
You are a senior API test automation engineer, expert in Rest-Assured + Java + Jackson + Lombok. You model API payloads as clean POJOs and refactor existing tests safely, one step at a time, with the test suite as your safety net.

CONTEXT:
- Cursor Agent mode, workspace = the Maven project from the previous steps (GET, POST, PUT/PATCH, DELETE and Auth tests, `BookingClient`, `BookingPayloadFactory`, `AuthHelper`, Allure, `testng.xml`). All tests currently use `Map<String, Object>` request bodies and `.body("path", ...)` string paths.
- API under test: Restful-Booker, https://restful-booker.herokuapp.com (source: https://restful-booker.herokuapp.com/apidoc/index.html). JSON shapes (verified live; `additionalneeds` may be absent, and a text `totalprice` comes back as null):
  - POST /auth request: {"username", "password"}. Response: {"token"} or {"reason"}.
  - Booking: {"firstname", "lastname", "totalprice", "depositpaid", "bookingdates": {"checkin", "checkout"}, "additionalneeds"}.
  - POST /booking response: {"bookingid", "booking": <Booking>}.
  - GET /booking response: [{"bookingid"}, ...].
- Dependencies already in `pom.xml`: Jackson databind, Lombok (provided). Rest-Assured uses Jackson automatically when it is on the classpath.
- Existing POJO package: `com.testtribe.api.pojo` (empty).

TASK:
Create POJOs and migrate the tests to them. Work in this order, finishing each step before the next:
1. Run `mvn clean test` first. Record the pass count as the BASELINE. If any test fails, stop and tell me (do not refactor on a red build).
2. Call the live API through Rest-Assured (`.extract().asString()`, no POJOs yet) for GET /booking/{id} (id from the list), POST /auth, and POST /booking (delete the booking afterwards). Show the three real JSON responses (max 6 lines each) and compare them with the JSON shapes above. If a field name or type differs, ask me before creating POJOs. Do not use curl.
3. Create these classes in `src/test/java/com/testtribe/api/pojo/`:
   - `BookingDates`: `checkin`, `checkout` (both String, format yyyy-MM-dd).
   - `Booking`: `firstname`, `lastname`, `totalprice` (Integer), `depositpaid` (Boolean), `bookingdates` (BookingDates), `additionalneeds`.
   - `BookingResponse`: `bookingid` (Integer), `booking` (Booking).
   - `BookingId`: `bookingid` (Integer), for the GET /booking list items.
   - `AuthRequest`: `username`, `password`.
   - `AuthResponse`: `token`, `reason`.
   Annotations on every class: Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`; Jackson `@JsonIgnoreProperties(ignoreUnknown = true)` and `@JsonInclude(JsonInclude.Include.NON_NULL)`.
   Use wrapper types (`Integer`, `Boolean`), never primitives, so a missing field is null instead of 0/false. This also makes PATCH possible with a `Booking` that sets only `firstname`.
4. Write `src/test/java/com/testtribe/api/pojo/PojoSerializationTest.java` (no network, uses Jackson `ObjectMapper` directly):
   a. shouldSerializeBookingToExpectedJson: build a `Booking` with the builder, serialize, assert the JSON has exactly the 6 expected keys with right values.
   b. shouldDeserializeBookingResponse: parse a hard-coded sample POST /booking response string into `BookingResponse`, assert fields.
   c. shouldIgnoreUnknownFields: JSON with an extra field deserializes without an exception.
   d. shouldOmitNullFieldsOnSerialize: `Booking` with only `firstname` serializes to `{"firstname":"..."}`.
   e. shouldDeserializeAuthFailure: `{"reason":"Bad credentials"}` gives `reason` set and `token` null.
   Register the class in `testng.xml` as `<test name="POJO Tests">`.
5. Migrate in this order. After EACH numbered item run `mvn clean test` and confirm the pass count equals BASELINE plus the new POJO tests. If it drops, fix before moving on:
   1. `BookingPayloadFactory`: methods return `Booking` (via the builder) instead of `Map`. Keep the same method names and data rules.
   2. `AuthHelper` and `BookingClient`: send `AuthRequest` and `Booking`, return typed values (`AuthResponse`, `BookingResponse`).
   3. `CreateBookingTest`: send `Booking`, read the response with `.as(BookingResponse.class)`, compare the whole object: `assertEquals(response.getBooking(), sentBooking)`. 
   4. `GetBookingTest`: deserialize GET /booking/{id} into `Booking`, GET /booking into `BookingId[]`. Keep the date format and `LocalDate` checks.
   5. `UpdateBookingTest` and `DeleteBookingTest`: POJO bodies; PATCH uses `Booking.builder().firstname("...").build()`.
   6. `AuthTest`: `AuthRequest` and `AuthResponse`.
6. Keep `Map` or raw JSON strings ONLY for tests that must send invalid shapes a POJO cannot express (missing `bookingdates`, text in `totalprice`, empty body). Add a one-line comment above each: `// Raw body on purpose: invalid shape`.
7. Run `mvn clean test` a final time.
8. If something fails and you cannot fix it in 3 attempts, stop and explain.

CONSTRAINTS:
- Java 17 + Lombok + Jackson + Rest-Assured + TestNG only. No other library (no Gson, no MapStruct, no Faker).
- POJOs are pure data: no logic, no static helpers, no Rest-Assured imports. No `System.out.println`.
- Do not change what any test verifies, only how it is written. Test names, `description`, Allure annotations, and test count stay the same.
- Do not change any behavior of the existing tests. Do not weaken or remove an assertion to get green.
- Use `@Builder` for creating objects in tests; no all-args constructor calls in tests.
- No logging framework yet. Do not touch files outside this workspace.

CLARITY (do this BEFORE step 1):
1. If any of `GetBookingTest`, `CreateBookingTest`, `UpdateBookingTest`, `DeleteBookingTest`, `AuthTest` is missing, stop and list what is missing. Change nothing.
2. If Lombok does not process (compile error such as "cannot find symbol getFirstname"), fix `annotationProcessorPaths` in `pom.xml` and tell me exactly what you changed.
3. If a field type is unclear from the real response (null, empty, or mixed values), ask me one precise question. Otherwise write "Ready." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED". For step 5, one line per sub-item with the pass count.
- After step 7 print exactly three parts and nothing else:
  A. Files created / changed (one per line, max 6-word purpose).
  B. Table: Step | Tests passed | Tests total (BASELINE row first, final row last).
  C. Before/after of ONE test: 5 lines of the old Map version and 5 lines of the new POJO version.

EXAMPLE (structure only; different API, do not reuse content):
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String street;
    private String city;
    private Integer zip;
}

// In a test:
Address sent = Address.builder().street("Main St").city("Berlin").zip(10115).build();
Address received = given().contentType(ContentType.JSON).body(sent)
    .when().post("/addresses")
    .then().statusCode(201)
    .extract().as(Address.class);
assertEquals(received, sent);