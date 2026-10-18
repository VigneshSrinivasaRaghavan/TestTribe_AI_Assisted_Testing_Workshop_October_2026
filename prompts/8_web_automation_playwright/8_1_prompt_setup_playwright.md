# Prompt 8.1: Installing and Setting Up a Playwright Project in Cursor

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. The prompt is self-contained.
**Use in:** Cursor Agent mode, in an empty folder opened as the workspace.
**Technique mix:** Role prompting + chain-of-thought (ordered steps with verification) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer with ten years of experience building Playwright + TypeScript frameworks. You set up projects that are minimal, reproducible, and ready to extend without rework.

CONTEXT:
- Cursor Agent mode, empty workspace root. You may run terminal commands.
- Application under test: https://www.saucedemo.com. Login: standard_user / secret_sauce (public demo credentials).
- Stack: Node.js, npm, Playwright Test, TypeScript. Browsers: Chromium, Firefox, WebKit.
- The project will later get data-driven tests and a Page Object Model.

TASK:
Set up a working Playwright + TypeScript project and prove it with one smoke test. Work in this order, finishing each step before the next:
1. Run `node -v` and `npm -v`.
2. `npm init -y`, then install `@playwright/test`, `typescript`, and `@types/node` as dev dependencies.
3. Install the Chromium, Firefox, and WebKit browsers.
4. Create `tsconfig.json` (suited for Playwright), `playwright.config.ts`, and `.gitignore` (node_modules, test-results, playwright-report, .env).
5. Write `tests/smoke/login.smoke.spec.ts`: open SauceDemo, log in, verify the Products page is shown (URL and title).
6. Add npm scripts: `test`, `test:headed`, `test:ui`, `report`.
7. Run the smoke test on all three browsers.
8. If it fails, read the error, fix the cause, re-run. Maximum 3 attempts, then stop and explain the blocker.

CONSTRAINTS:
- TypeScript only. Playwright Test only: no Jest, Mocha, Cypress, or other packages.
- Do not use `npm init playwright@latest`. Create each file manually.
- `playwright.config.ts` must have: `testDir: './tests'`, `baseURL: 'https://www.saucedemo.com'`, projects chromium/firefox/webkit, `fullyParallel: true`, `retries: 0`, reporter `html` + `list`, `trace: 'on-first-retry'`, `screenshot: 'only-on-failure'`, `video: 'retain-on-failure'`, `timeout: 30000`, `expect.timeout: 5000`.
- Smoke test: `page.goto('/')` (no full URL); locators via `getByRole`, `getByPlaceholder`, or `[data-test="..."]` (no XPath); web-first assertions only (`toBeVisible`, `toHaveURL`, `toHaveText`); no `waitForTimeout`.
- No Page Objects, fixtures, data files, or CI files now. Do not touch files outside this workspace.

CLARITY (do this BEFORE step 1):
1. If the workspace already has a package.json or Playwright files, stop and ask me whether to reuse or replace them. Delete nothing without my answer.
2. If Node.js is missing or older than 20, stop and tell me the exact upgrade action.
3. Otherwise write "Environment OK." and continue.
Never fill a gap with an assumption. Never skip a step silently.

FORMAT:
- Progress, one line per step: "Step N: <what happened> - DONE" or "- FAILED".
- Test file name: `<feature>.<type>.spec.ts`. Test title: sentence case, starts with a verb.
- After step 7 print exactly three parts and nothing else:
  A. Files created (one per line, max 6-word purpose).
  B. Commands: all browsers, one browser, headed, UI mode, open report.
  C. Table: Browser | Result | Duration.

EXAMPLE (structure only; different app, do not reuse content):
import { test, expect } from '@playwright/test';

test('Show results for a valid keyword', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('searchbox', { name: 'Search' }).fill('laptop');
  await page.getByRole('button', { name: 'Search' }).click();
  await expect(page).toHaveURL(/search\?q=laptop/);
});
```

---

## Trainer notes

- **Why Agent mode:** students watch Cursor run commands, read a failure, and self-correct.
- **Why no `npm init playwright@latest`:** manual setup makes every file explainable. It is the fast path after the workshop.
- **CLARITY demo:** run the prompt in a folder that already has a `package.json`. Cursor should stop and ask.
- **Expected outcome:** 3 tests passed (1 test x 3 browsers), HTML report in `playwright-report/`.
- **SauceDemo locators:** `[data-test="username"]`, `[data-test="password"]`, `[data-test="login-button"]`. Products page: title "Products", URL `/inventory.html`.
- **Likely failure:** WebKit system dependencies on Linux. Cursor should handle it in step 8.
