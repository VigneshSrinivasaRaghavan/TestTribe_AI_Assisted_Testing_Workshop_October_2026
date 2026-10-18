# Prompt 8.2: Crafting Basic Playwright Tests with Cursor

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `8_2_story_SD-101_login.md` (use `@` in Cursor chat). Requires the project from Prompt 8.1.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (inspect, write, run) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer specialising in Playwright + TypeScript. You write short, readable, stable tests any tester can maintain.

CONTEXT:
- Workspace is the Playwright project from setup (baseURL https://www.saucedemo.com, tests in ./tests).
- The attached story (AC1 to AC8) is the only source of truth for expected behaviour.

TASK:
Write 4 to 6 tests (your choice, positive and negative) in tests/login/login.spec.ts so every AC is covered at least once. In order:
1. Inspect the login page with a throwaway script (`await page.locator('body').ariaSnapshot()` plus the `data-test` attributes). Delete the script afterwards.
2. Write the tests.
3. Run `npx playwright test tests/login` on all three browsers.
4. If a test fails, fix the cause in the test (never weaken an assertion) and re-run. Maximum 3 attempts, then stop and explain.

CONSTRAINTS:
- Test only what the story states. Use its exact messages in assertions. Skip "Out of Scope".
- Relative URLs only (`page.goto('/')`). Locators: `getByRole`, `getByPlaceholder`, or `[data-test="..."]`. No XPath.
- Web-first assertions only. No `waitForTimeout`.
- Independent tests, one primary behaviour each. Credentials as constants at the top of the file.
- No Page Objects, fixtures, or data files. Do not edit playwright.config.ts.

CLARITY (before step 1):
If an AC is ambiguous or playwright.config.ts is missing, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- One `test.describe('Login', ...)`. Test titles: sentence case, start with a verb, prefixed with the AC id, e.g. "AC1: Log in with valid credentials".
- Final summary, nothing else: A. Table AC | Test title. B. Table Browser | Passed | Failed. C. Any AC not covered, with the reason.

EXAMPLE (structure only; different feature):
test('AC1: Show results for a valid keyword', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('searchbox', { name: 'Search' }).fill('laptop');
  await page.getByRole('button', { name: 'Search' }).click();
  await expect(page.getByRole('heading', { name: 'Results' })).toBeVisible();
});

USER STORY:
See the attached file.
```
