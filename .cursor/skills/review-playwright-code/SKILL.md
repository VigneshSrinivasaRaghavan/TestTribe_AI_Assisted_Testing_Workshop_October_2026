---
name: review-playwright-code
description: >-
  Reviews Playwright TypeScript in playwright-ts against this repo's locator
  priority, BasePage page objects, test isolation, and web-first assertions.
  Use when the user asks to review, critique, check, or comment on a
  teammate's Playwright spec, page object, test diff, or pull request in
  playwright-ts.
---

# Review Playwright code

Review the diff or the files the user named. Read `playwright-ts/playwright.config.ts`, `tests/pages/basePage.ts`, and the nearest existing spec for the same app before judging. Do not rewrite the code. Do not apply fixes unless the user asks.

## Classify each file first

| File | Bar |
|---|---|
| `tests/<number>_*.spec.ts` (`1_` through `24_`) | Workshop lesson. Review whether the lesson still demonstrates its concept and still runs. Do not demand a page-object rewrite of a lesson. |
| `tests/pages/*.ts`, `tests/saucedemo-login.spec.ts`, `tests/e2e-order-complete-flow.spec.ts`, any new `tests/*.spec.ts` | Product test. Apply the full rubric. |
| `playwright.config.ts`, `package.json` | Call out only if the diff changes projects, `testDir`, reporters, `forbidOnly`, retries, or the `test` script (it is pinned to `tests/1_login.spec.ts` and `pretest` force-installs browsers). |

Known debt — mention only if the diff copies or extends it, not as a finding on untouched lines:

- `BasePage.b_waitForPageToLoad` sleeps 1000ms after `networkidle`.
- `adminPage.ts` uses a positional XPath for the employee name field.
- Numbered lessons use raw locators, `console.log` hooks, and public demo credentials on purpose.

`Admin` / `admin123` and `standard_user` / `secret_sauce` are public demo accounts. Do not flag them as secrets. Flag any other credential, token, or `.env` value committed in the diff.

## Rubric

Judge only what the diff introduces or changes. `fullyParallel: true`. Projects are chromium, firefox, and webkit. `baseURL` is unset. `forbidOnly` is on in CI. Trace, screenshot, and video already record on failure — do not ask for manual screenshots.

### Blocker

The test can pass while the behavior is broken, or CI will reject it.

- `test.only` left in source.
- No web-first `expect` on the outcome the title claims (a login that only clicks Logout is a blocker on a product spec).
- `page.waitForTimeout`, a new `networkidle` wait, or `force: true` added to hide a race.
- Test B depends on test A (shared browser state, order, a user the previous test created).
- New positional XPath, or a locator that matches a full generated `class` string.
- `getByRole` / `getByLabel` / `getByText` introduced without the user having asked. This repo's locator API is `page.locator()` + CSS. See priority below.
- API test drives the browser, or a UI test uses `{ request }` for a flow that has a page.
- Hard-coded OrangeHRM username or password when `tests/testData/orangeHrmLoginData.json` already has the row.
- A secret that is not one of the public demo accounts above.

### Should fix

Wrong structure for this repo. The test may still pass.

- Stable attribute ignored. Priority, first match wins: `id` (`#id`), `name`, `data-test` / `data-test-id`, `placeholder`, then partial `class` combined with another attribute (`input[class^="oxd-input"][placeholder="Username"]`). CSS over XPath. XPath only for text or when no attribute exists.
- OrangeHRM UI flow that ignores `LoginPage` / `DashboardPage` / `AdminPage`, or a new screen with locators inlined in the spec instead of a `tests/pages/` class extending `BasePage`.
- Page locator that is not `private readonly`, or an action that clicks/fills without `b_clickElement` / `b_fillField` / `b_navigateTo`.
- Assertion stuffed inside a page object or a hook. Hooks navigate and authenticate only.
- Same login block pasted across tests in one file — belongs in `test.beforeEach`.
- SauceDemo locator that is not `[data-test="…"]` when that attribute exists on the element (see `saucedemo-login.spec.ts`).
- New call to `b_waitForPageToLoad`.
- Spec added by editing a numbered lesson file instead of a new `tests/<kebab-name>.spec.ts`.
- `test.skip` / `test.fail` / `test.fixme` / `test.slow` with no condition or reason string.
- Title describes clicks, or several behaviors share one `test`.

### Nit

- Unused `expect` import, quote drift, comment that restates the code.
- Missing `@smoke` / `@sanity` / `@regression` when surrounding tests in the same feature use tags. Do not require a tag on an untagged feature.
- Allure `step()` missing on a long E2E. Do not require steps on a short test. When present, names should be `Given` / `When` / `Then` and must match the action inside (see `tests/23_allureWithSteps.spec.ts`).

Do not recommend a `getByRole` migration, a custom fixture framework, Playwright component tests, or a `baseURL` change as part of the review.

## Output

Use this shape. Cite `path:line` for every finding. If a section is empty, write `None`.

```markdown
## Verdict
Request changes | Approve

## Findings

### Blocker
- `tests/foo.spec.ts:12` — <what is wrong>. <one-line fix>.

### Should fix
- `tests/pages/fooPage.ts:20` — <what is wrong>. <one-line fix>.

### Nit
- None

## Leave as-is
- <one line on what already matches this repo, or "Nothing to call out.">
```

`Request changes` if any Blocker or Should fix exists. `Approve` only when both are empty.

Sort findings by file, then line. One sentence for the defect, one sentence for the fix. The fix names the locator, helper, or assertion to use. No preamble, no rewritten file, no praise paragraph.
