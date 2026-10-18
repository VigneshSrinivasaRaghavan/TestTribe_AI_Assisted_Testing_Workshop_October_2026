# Prompt 8.4: Automating Checkboxes, Radio Buttons, Dropdowns, and Inputs

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** Nothing. Requires the project from Prompt 8.1.
**Use in:** Cursor Agent mode.
**Technique mix:** Role prompting + chain-of-thought (inspect, write, run) + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior test automation engineer specialising in Playwright + TypeScript. You use the correct Playwright API for each control type and assert the resulting state, not just the action.

CONTEXT:
- Workspace is the Playwright project from setup.
- Practice site (use absolute URLs, it differs from baseURL): https://practice.expandtesting.com
  /checkboxes (two checkboxes), /radio-buttons (colour and sport groups), /dropdown (a simple select), /inputs (number, text, password, date inputs, plus Display Inputs and Clear Inputs buttons).

TASK:
Write one spec per control in tests/forms/, 2 to 3 tests each. In order:
1. Inspect each page with a throwaway script (`ariaSnapshot()` plus control HTML). Delete it afterwards.
2. Write:
   - checkboxes.spec.ts: initial state, check, uncheck.
   - radio.spec.ts: select an option in each group; verify only that option is selected in its group.
   - dropdown.spec.ts: select by value, by label, and by index; verify the selected value.
   - inputs.spec.ts: fill all four inputs, click Display Inputs, verify each output; click Clear Inputs, verify all empty.
3. Run `npx playwright test tests/forms` on all three browsers.
4. If a test fails, fix the cause (never weaken an assertion) and re-run. Maximum 3 attempts, then stop and explain.

CONSTRAINTS:
- APIs: checkbox/radio `check()`, `uncheck()`, `setChecked()` with `toBeChecked()`; select `selectOption()` with `toHaveValue()`; inputs `fill()` / `clear()` with `toHaveValue()`.
- Never `click()` to toggle a checkbox or radio. Never `type()` or `pressSequentially()` unless the page needs key events.
- Locators: `getByRole`, `getByLabel`, or the id only when no accessible name exists. No XPath.
- Web-first assertions only. No `waitForTimeout`. Each test starts with `page.goto(...)`.
- No Page Objects, fixtures, or data files. Do not edit playwright.config.ts.
- If a page behaves differently from this description, report it. Do not invent behaviour.

CLARITY (before step 1):
If a page is unreachable or its controls differ from CONTEXT, ask numbered questions (max 3, one line each) and STOP. Otherwise write "No clarifications needed." Never assume.

FORMAT:
- Test titles: sentence case, start with a verb.
- Final summary, nothing else: A. Table Control | Playwright API | Assertion | File. B. Table Browser | Passed | Failed. C. Any page behaviour that differed from expectation.

EXAMPLE (structure only; different page):
test('Check the newsletter checkbox', async ({ page }) => {
  await page.goto('https://example.com/subscribe');
  const newsletter = page.getByLabel('Newsletter');
  await expect(newsletter).not.toBeChecked();
  await newsletter.check();
  await expect(newsletter).toBeChecked();
});
```
