---
name: add-playwright-test
description: >-
  Adds a Playwright TypeScript test under playwright-ts using this repo's
  page objects, BasePage b_* helpers, CSS locator priority, test data files,
  and web-first assertions. Use when the user asks to add, write, scaffold,
  automate, or extend a Playwright spec, E2E flow, API test, page object, or
  test data in playwright-ts.
---

# Add a Playwright test

Work only inside `playwright-ts/`. Read the nearest existing spec and any page class you will touch before writing. Match that file's shape. Do not restyle unrelated files.

Numbered lesson files (`tests/1_login.spec.ts` … `tests/24_apiTest.spec.ts`) are workshop demos. Never append a new scenario to them. New tests are a new `tests/<kebab-name>.spec.ts`.

## Do not

- Do not use `getByRole`, `getByLabel`, `getByPlaceholder`, or `getByText` as the default locator. This repo uses `page.locator()` with CSS attribute selectors. `getBy*` only if the user explicitly asks.
- Do not leave `test.only`, `test.fixme` without a reason, or `page.waitForTimeout`.
- Do not edit `playwright.config.ts`, `package.json`, or `BasePage` unless the user asked. `baseURL` is unset — pass full URLs.
- Do not add a page-object method that only wraps one `locator` call the test uses once.
- Do not run `npm test`, `npm run test`, or `npm run test:*`. Those scripts reinstall browsers and are pinned to `tests/1_login.spec.ts`.
- Do not invent a locator you have not seen in an existing spec, the DOM, or a user-supplied snippet. Say which source it came from.

## Route the test

| Target | Where behavior lives | How to write it |
|---|---|---|
| OrangeHRM `https://opensource-demo.orangehrmlive.com/` | `tests/pages/loginPage.ts`, `dashboardPage.ts`, `adminPage.ts`; creds in `tests/testData/orangeHrmLoginData.json` | Page object. Reuse `LoginPage`, `DashboardPage`, `AdminPage`. Add a class only for a screen that has no page yet. |
| SauceDemo `https://www.saucedemo.com/` | `tests/saucedemo-login.spec.ts`, `tests/e2e-order-complete-flow.spec.ts`. No page objects. | Inline `page.locator('[data-test="…"]')` until the same locator block is copied into a second spec. Then extract `tests/pages/`. |
| HTTP API | `tests/24_apiTest.spec.ts` | `{ request }` fixture. No `page`. Assert status, then body fields. |
| Anything else the user names | closest spec | Follow that spec. Do not retarget OrangeHRM or SauceDemo. |

`fullyParallel: true` and three projects (chromium, firefox, webkit). Each test must log in itself, must not depend on another test's order, and must not mutate shared server state.

## Locator priority

Use the first one that exists on the element. Prefer CSS. Use XPath only for visible text or when no attribute exists.

1. `id` → `#user-name` or `input#user-name`
2. `name` → `input[name="username"]`
3. `data-test` or `data-test-id` → `[data-test="username"]`
4. `placeholder` → `[placeholder="Username"]`
5. `class` last. Never match the full generated class string. Combine a partial class with another attribute: `input[class^="oxd-input"][placeholder="Username"]`. Partial operators: `^=` starts-with, `$=` ends-with, `*=` contains.

Ban positional XPath (`(//input[...])[2]`) in new code. If the only existing locator is positional (see `adminPage.ts`), do not copy it into a new page; find an attribute or ask.

## Page objects

New and edited UI flows for OrangeHRM go through `tests/pages/`. Pattern (see `loginPage.ts`):

- `export class XxxPage extends BasePage`
- `private readonly` locators, assigned only in the constructor
- Action methods call `b_fillField`, `b_clickElement`, `b_navigateTo`, `b_selectStaticDropDown`, or `b_selectDynamicDropDown`
- Assertions stay in the spec, not in the page class

`BasePage` already waits for visible before click and fill. Do not add another wait in the spec for those actions. Do not call `b_waitForPageToLoad` from new tests — it sleeps 1s after `networkidle`.

OrangeHRM credentials come from `tests/testData/orangeHrmLoginData.json` (`validUsername`, `validPassword`, `invalidUsername`, `invalidPassword`). SauceDemo's `standard_user` / `secret_sauce` may stay inline; they match the existing specs. Any other account goes in `tests/testData/`.

## Spec shape

```typescript
import { test, expect } from '@playwright/test';
import { LoginPage } from './pages/loginPage';
import * as orangeHrmData from './testData/orangeHrmLoginData.json';

test.describe('OrangeHRM login', () => {
  test('shows the dashboard after valid credentials @smoke', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.b_navigateTo('https://opensource-demo.orangehrmlive.com/');
    await loginPage.enterUsername(orangeHrmData.validUsername);
    await loginPage.enterPassword(orangeHrmData.validPassword);
    await loginPage.clickLogin();

    await expect(page).toHaveURL(/\/dashboard\/index/);
  });
});
```

Rules for that shape:

- One behavior per `test`. Title says the expected outcome, not the clicks.
- Tag with `@smoke`, `@sanity`, or `@regression` only when the user named a suite. Default is no tag.
- At least one web-first assertion on the outcome (`toHaveURL`, `toHaveTitle`, `toHaveText`, `toContainText`, `toBeVisible`, `toBeHidden`, `toHaveCount`). A click sequence with no `expect` is incomplete.
- Negative cases assert the error text and that the URL did not change.
- Shared login for several tests in the same file goes in `test.beforeEach`. Logout goes in `test.afterEach` only when a later test needs a clean session.
- Allure: wrap steps with `step` from `allure-js-commons` only when the user asks for an Allure scenario or the test is a multi-page E2E. Name steps `Given` / `When` / `Then`. See `tests/23_allureWithSteps.spec.ts`.
- Parameterize with an array + `forEach` and a unique title (see `tests/19_parametrizeTest.spec.ts`) when the same steps run on multiple data rows. Do not copy the test body.

## After writing

From `playwright-ts/`:

```bash
npx playwright test tests/<file>.spec.ts --project=chromium
```

If it fails, fix the spec. Do not raise timeouts, add retries, or set `force: true` to make it pass. Re-run the same command. Report the command and the pass/fail line. Do not run firefox or webkit unless the user asks.
