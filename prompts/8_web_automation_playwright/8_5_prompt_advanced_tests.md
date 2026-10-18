# Prompt 8.5: Developing Advanced Playwright Tests with Cursor

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. Requires the project from Prompt 8.1.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (setup, write, run) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer specialising in Playwright + TypeScript. You build fast, stable end-to-end tests that reuse authentication, split flows into named steps, and verify business calculations.

CONTEXT:
- Workspace is the Playwright project from setup (baseURL https://www.saucedemo.com). Login: standard_user / secret_sauce.
- Verify on the live pages, do not assume: 6 products, a sort dropdown (including price low to high), cart badge, 3-page checkout (information, overview with tax, complete).
- Checkout errors: "Error: First Name is required" (also Last Name, Postal Code). Confirmation: "Thank you for your order!".

TASK:
Build an authenticated end-to-end suite. In order:
1. Inspect the Products, Cart, Checkout and Complete pages with a throwaway script (`ariaSnapshot()` plus `data-test` attributes). Delete it afterwards.
2. Reusable login: create tests/auth.setup.ts that logs in once and saves `storageState` to playwright/.auth/user.json. In playwright.config.ts add a `setup` project (testMatch /auth\.setup\.ts/) and make the three browser projects depend on it. Do not set storageState globally. Add `playwright/.auth` to .gitignore.
3. Write tests/e2e/shopping.spec.ts, with `test.use({ storageState: 'playwright/.auth/user.json' })`, exactly 5 tests:
   a. Add two products: cart badge shows 2 and the cart page lists both.
   b. Remove one product: badge shows 1.
   c. Sort by price low to high: displayed prices are ascending.
   d. Buy two products: Item total equals the sum of product prices, Total equals Item total plus Tax, then finish and see the confirmation message.
   e. Checkout with empty First Name shows its error.
4. Run `npx playwright test` on all three browsers (existing login tests must still pass).
5. If a test fails, fix the cause (never weaken an assertion) and re-run. Maximum 3 attempts, then stop and explain.

CONSTRAINTS:
- Wrap each logical phase in `test.step('<action>', ...)`. Use `expect.soft` for the price and total checks in (d), normal `expect` elsewhere.
- Read prices from the page and compute totals in code (strip "$", parse to number). No hardcoded totals. For (c) compare against a sorted copy of all prices, not just first and last.
- Put all tests in `test.describe('Shopping', { tag: '@regression' }, ...)`.
- Locators: `getByRole`, `[data-test="..."]`. No XPath. Web-first assertions only. No `waitForTimeout`.
- Independent tests, each starting at `page.goto('/inventory.html')`.
- No Page Objects or data files. Do not change existing tests. No tests beyond the 5.

CLARITY (before step 1):
If the project is missing, SauceDemo differs from CONTEXT (e.g. no tax shown), or anything is ambiguous, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- Test titles: sentence case, start with a verb.
- Final summary, nothing else: A. Files created or changed (6-word purpose each). B. Table Browser | Passed | Failed.

EXAMPLE (structure only; different app):
test.describe('Cart', { tag: '@regression' }, () => {
  test.use({ storageState: 'playwright/.auth/user.json' });
  test('Add a product to the cart', async ({ page }) => {
    await page.goto('/products');
    await test.step('Add the first product', () => page.getByRole('button', { name: 'Add to cart' }).first().click());
    await test.step('Verify the cart count', () => expect(page.getByTestId('cart-count')).toHaveText('1'));
  });
});
```
