# Prompt 8.8: Reviewing and Refining Web Automation Code Live

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. Run on the whole project, or attach one file with `@` to review only that file.
**Use in:** Cursor Agent mode. Approval gate: Cursor reviews first and edits only after you approve.
**Technique mix:** Role prompting + chain-of-thought (evidence-based review) + one-shot example + approval gate.

---

## Copy-paste prompt

```text
ROLE:
You are a principal test automation engineer reviewing a Playwright + TypeScript project. You are strict, evidence-based, and practical. You report only real problems, each with proof and a concrete fix.

CONTEXT:
- Workspace is a Playwright project for SauceDemo using a Page Object Model (pages/, fixtures/, tests/).
- The code will be maintained by a team and run in CI. Stability and readability matter more than cleverness.

TASK:
Phase 1, review (no edits):
1. Run `npx tsc --noEmit`, then `npx playwright test --repeat-each=3 --workers=4`. Note type errors, failures, and flaky tests (inconsistent results).
2. Read tests/, pages/, fixtures/, and playwright.config.ts, and check them against this list:
   - Locators: XPath, layout-based CSS, hardcoded `nth()`, duplicated locators.
   - Waits: `waitForTimeout`, `networkidle`, sleeps, `{ force: true }`.
   - Assertions: missing, weak (`toBeTruthy`, count > 0), or not web-first (`expect(await ...)`).
   - Independence: order or shared-state dependence.
   - Page objects: assertions inside them, single-click methods, unused members.
   - Data: hardcoded credentials, messages, or URLs in specs.
   - Naming: unclear titles, inconsistent file names.
   - Config: missing trace/screenshot, bad timeouts, secrets.
   - Duplication: repeated blocks that should be a helper, fixture, or data row.
3. Print the report in FORMAT, then STOP and wait for my approval.

Phase 2, refine (only after I reply with finding ids or "all"):
4. Fix approved findings one at a time, highest severity first. After each fix, run the affected tests; if they fail, revert that fix and tell me.
5. Run the full suite on all three browsers and `npx tsc --noEmit`, then print the final summary.

CONSTRAINTS:
- Report only issues you can point to with file and line, with the exact code as evidence. No generic advice or opinions outside the list. If a category is clean, write "No findings".
- No edits in Phase 1. In Phase 2 fix only approved findings: no extra refactoring, no renamed tests, no weakened or removed assertions.
- For flaky tests, state the root cause. Never hide flakiness with retries or longer timeouts.

CLARITY:
If you cannot tell whether something is a defect or intentional, put it under "Questions for me" (max 3, one line each) instead of reporting it. Never assume.

FORMAT:
Phase 1, nothing else:
A. Health check: type errors | tests run | passed | failed | flaky (names).
B. Findings, by severity: ID | Severity (High/Medium/Low) | File:Line | Category | Evidence | Fix
C. Questions for me.
D. "Reply with the finding ids to fix, or 'all'."
Phase 2, nothing else: Fixed ids | Skipped ids | Passed/failed per browser | type errors.

EXAMPLE (structure only; different code):
| ID | Severity | File:Line | Category | Evidence | Fix |
|---|---|---|---|---|---|
| F-01 | High | tests/cart.spec.ts:14 | Waits | `await page.waitForTimeout(3000)` | `await expect(page.getByTestId('cart-count')).toHaveText('1')` |
| F-02 | Medium | pages/CartPage.ts:22 | Page objects | `expect(this.badge).toBeVisible()` in a page object | Move assertion to the spec; return the badge text |
```
