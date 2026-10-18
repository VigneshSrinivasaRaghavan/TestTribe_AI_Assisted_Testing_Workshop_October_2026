# Prompt 8.6: Data-Driven Testing with Playwright

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `8_2_story_SD-101_login.md`. Requires the project from Prompt 8.1.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (design data, wire, run) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer specialising in Playwright + TypeScript. You separate test logic from test data so new scenarios are added by editing data, not code.

CONTEXT:
- Workspace is the Playwright project from setup (baseURL https://www.saucedemo.com).
- The attached login story (SD-101) is the source of truth for login messages.
- Checkout "Your Information" requires First Name, Last Name, and Postal Code. Errors: "Error: First Name is required", "Error: Last Name is required", "Error: Postal Code is required".

TASK:
Make login and checkout-information validation data-driven. In order:
1. Create tests/data/login.data.json with 6 to 8 rows `{ id, description, username, password, expected: "success" | "error", message }`. Derive rows from AC1 to AC5, plus 2 rows for a leading/trailing space and an uppercase username. For rows the story does not cover, observe the real behaviour on the live site first and record it.
2. Create tests/data/checkout-info.data.json with 4 rows `{ id, description, firstName, lastName, postalCode, expected, message }`: all valid, missing first name, missing last name, missing postal code.
3. Write tests/data-driven/login.data.spec.ts and checkout-info.data.spec.ts. Each loops `for (const row of rows)` and creates one test per row, titled `${row.id}: ${row.description}`.
   - Login spec starts unauthenticated: `test.use({ storageState: { cookies: [], origins: [] } })`.
   - Checkout spec logs in via the UI in `beforeEach`, adds one product, opens checkout step one.
4. Type the rows in tests/data/types.ts. Enable `resolveJsonModule` and `esModuleInterop` in tsconfig.json only if needed.
5. Run `npx playwright test tests/data-driven` on all three browsers.
6. If a test fails, decide whether data or test is wrong, fix it (never weaken an assertion), and re-run. Maximum 3 attempts, then stop and explain.

CONSTRAINTS:
- One generic test body per spec: no copy-pasted tests, no `if (row.id === ...)`. The only branch allowed is success vs error.
- Credentials, inputs, and messages live only in the JSON files.
- Synthetic data only, no attack payloads.
- Locators: `getByRole`, `getByPlaceholder`, `[data-test="..."]`. Web-first assertions only. No `waitForTimeout`.
- No Page Objects or fixtures. Do not edit existing tests or playwright.config.ts.

CLARITY (before step 1):
If the story is not attached, a row's real behaviour contradicts the story, or anything is ambiguous, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- JSON: 2-space indent, success rows first. Row ids: LOGIN-01..., CHK-01...
- Final summary, nothing else: A. Table Row id | Description | Expected | chromium | firefox | webkit (Pass/Fail). B. Rows whose real behaviour differed from the story, with the observed message.

EXAMPLE (structure only; different feature):
[{ "id": "AGE-01", "description": "Accept lower boundary", "age": "18", "expected": "success", "message": "" }]

for (const row of rows) {
  test(`${row.id}: ${row.description}`, async ({ page }) => {
    await page.goto('/register');
    await page.getByLabel('Age').fill(row.age);
    await page.getByRole('button', { name: 'Submit' }).click();
    if (row.expected === 'error') await expect(page.getByRole('alert')).toHaveText(row.message);
    else await expect(page).toHaveURL(/welcome/);
  });
}

USER STORY:
See the attached file.
```
