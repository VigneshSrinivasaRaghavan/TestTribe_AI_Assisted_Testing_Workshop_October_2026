# Prompt 8.3: Identifying Web Elements and Writing Selectors with AI

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. Requires the project from Prompt 8.1.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (inspect, rank, verify) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer and locator-strategy expert for Playwright + TypeScript. You choose locators that survive UI redesigns and justify every choice.

CONTEXT:
- Workspace is the Playwright project from setup (baseURL https://www.saucedemo.com).
- Pages: login (/) and Products (/inventory.html, after login as standard_user / secret_sauce).
- Elements: Login: username, password, Login button, error message, error close button. Products: page title, burger menu button, Logout link, sort dropdown, cart link, cart badge, first product's name, price, and Add to cart button.

TASK:
Produce the best verified locator for every element above. In order:
1. Inspect each page with a throwaway script (`ariaSnapshot()` plus the HTML of each element). Delete it afterwards.
2. Per element, pick by priority: getByRole (with name) > getByLabel / getByPlaceholder / getByText > `[data-test="..."]` > CSS by id/class.
3. Verify with a throwaway test on chromium: each locator resolves to exactly 1 element (`toHaveCount(1)`). Fix failures, maximum 3 attempts. Then delete the test.
4. Save the result in tests/locators/saucedemo.locators.ts.

CONSTRAINTS:
- Every locator comes from the inspection in step 1, never from memory of the site.
- Exactly one match per locator. If an element has several matches (e.g. product buttons), scope to a parent, or use `.first()` and say why.
- No XPath, no absolute CSS paths, no generated class names, no hardcoded `nth()` unless unavoidable (then flag it).
- Locators only: no Page Objects or tests.

CLARITY (before step 1):
If an element cannot be found or is ambiguous, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- The file exports one object per page; each value is a function `(page: Page) => Locator`, e.g. `export const loginLocators = { username: (page: Page) => page.getByPlaceholder('Username') };`
- Final summary, one table, nothing else: Page | Element | Locator | Strategy (Role/Label/Text/data-test/CSS) | Why chosen (max 10 words)

EXAMPLE (structure only; different app):
| Page | Element | Locator | Strategy | Why chosen |
|---|---|---|---|---|
| Search | Search box | page.getByRole('searchbox', { name: 'Search' }) | Role | Accessible name is stable |
| Search | Submit | page.locator('[data-test="search-submit"]') | data-test | No accessible name present |
```
