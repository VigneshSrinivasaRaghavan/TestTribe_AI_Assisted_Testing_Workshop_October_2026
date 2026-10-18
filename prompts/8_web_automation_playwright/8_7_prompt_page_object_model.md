# Prompt 8.7: Building the Page Object Model (POM) Framework

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. Requires the tests from Prompts 8.2, 8.5, and 8.6 in the project.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (baseline, refactor, prove no regression) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation architect specialising in Playwright + TypeScript. You refactor working tests into a small, readable Page Object Model without changing what the tests verify.

CONTEXT:
- Workspace is the Playwright project from setup (SauceDemo, baseURL https://www.saucedemo.com).
- In scope: tests/login/login.spec.ts, tests/e2e/shopping.spec.ts, tests/data-driven/*.data.spec.ts. Out of scope: tests/forms/ (different site) and tests/auth.setup.ts.
- Goal: remove duplicated locators and UI steps from specs by moving them into page objects exposed as Playwright fixtures.

TASK:
In order:
1. Baseline: run `npx playwright test` on all three browsers and record the passed count. If any test fails, stop and ask me.
2. Read the in-scope specs; list the pages touched and the repeated actions.
3. Create in pages/: LoginPage, InventoryPage, CartPage, CheckoutPage (step one, overview, complete). Add a BasePage only if 2 or more pages share real behaviour.
4. Create fixtures/pages.fixture.ts: extend `test` from '@playwright/test' with one fixture per page object, and re-export `expect`.
5. Refactor each in-scope spec to import `test` and `expect` from the fixtures file and use the page objects. Keep every test title, tag, and `test.step`.
6. Re-run `npx playwright test` on all three browsers. The passed count must equal the baseline. If not, fix the cause and re-run. Maximum 3 attempts, then stop and explain.

CONSTRAINTS:
- Page objects take `page: Page` in the constructor and expose locators as `readonly` properties.
- Methods express user intent (`login(user, pass)`, `addProductToCart(name)`, `fillShippingInfo(info)`), not single clicks.
- No assertions inside page objects. Assertions stay in specs. Page objects may return values (`getCartCount()`, `getItemPrices()`).
- Reuse the locators already in the specs. No XPath, no `waitForTimeout`.
- Do not add, remove, or rename tests, or change what they verify. Do not edit playwright.config.ts or the JSON data.
- Build only what the current specs need.

CLARITY (before step 1):
If an in-scope spec is missing, or cannot be refactored without changing its meaning, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- Files: pages/<Name>Page.ts (PascalCase, class name ends with "Page"), fixtures/pages.fixture.ts. Method names start with a verb.
- Final summary, nothing else: A. Table Page object | Locators | Methods. B. Table Spec | Lines before | Lines after. C. Passed count, baseline vs after, per browser.

EXAMPLE (structure only; different app):
// pages/SearchPage.ts
export class SearchPage {
  readonly searchBox = this.page.getByRole('searchbox', { name: 'Search' });
  constructor(private readonly page: Page) {}
  async search(keyword: string) { await this.searchBox.fill(keyword); await this.page.keyboard.press('Enter'); }
}
// fixtures/pages.fixture.ts
export const test = base.extend<{ searchPage: SearchPage }>({
  searchPage: async ({ page }, use) => { await use(new SearchPage(page)); },
});
export { expect };
```
