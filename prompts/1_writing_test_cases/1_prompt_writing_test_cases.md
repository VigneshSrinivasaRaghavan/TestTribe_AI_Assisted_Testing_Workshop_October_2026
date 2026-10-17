# Prompt 1: Writing Test Cases Using AI

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `story_ECOM-142_apply_coupon.md`
**Technique mix:** Role prompting + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior QA engineer with ten years of experience in functional testing of e-commerce web applications. You write test cases that are thorough, unambiguous, and ready to execute by any tester without clarification.

CONTEXT:
- The application is a web store (desktop and mobile browsers) with a checkout flow.
- The attached document is a JIRA user story with acceptance criteria (AC). Treat it as the only source of truth.
- The test cases will be imported into our test management tool, so structure matters.

TASK:
Generate exactly 6 test cases for the attached user story: 2 Positive, 2 Negative, and 2 Edge Case.
Together they must cover every acceptance criterion (AC1 to AC7) at least once.

CONSTRAINTS:
- Test only the behaviour described in the story. Do not invent features, messages, or rules.
- Use the exact UI messages written in the acceptance criteria.
- Do not include performance, security, or accessibility tests.
- Do not cover items listed under "Out of Scope".
- Use concrete test data (coupon codes, amounts) taken from the story. Where the story gives none, use clearly labelled sample values.
- One test case verifies one primary behaviour.
- Prioritise: High for core money-impacting flows, Medium for validation and messages, Low for cosmetic or rare cases.

CLARITY (do this BEFORE writing test cases):
1. Read the story and list every ambiguity, missing rule, or contradiction that would change a test's steps or expected result.
2. If you find any, ask me numbered questions, max 5, one line each, and STOP. Do not write test cases until I answer.
3. If you find none, write "No clarifications needed." and continue.
Never fill a gap with your own assumption.

FORMAT:
Write each test case as the following fields, in this exact order, one field per line:

Test Case ID:
Title:
Precondition:
Steps:
Expected Result:
Priority:
Type:
Linked AC:

Rules:
- Test Case ID: TC-001, TC-002 and so on.
- Title: sentence case, starts with a verb or outcome, max 12 words.
- Steps: numbered, one action per step.
- Expected Result: observable and specific, no words like "works" or "correctly".
- Priority: High, Medium, or Low.
- Type: Positive, Negative, or Edge Case.
- Linked AC: the AC id(s) covered, for example AC1, AC7.
After the 6 test cases, add a small coverage matrix: AC id to test case ids. No other commentary.

EXAMPLE:
Test Case ID: TC-001
Title: Log in successfully with valid credentials
Precondition: User has a registered account with a verified email
Steps:
1. Navigate to the login page.
2. Enter a valid registered email.
3. Enter the correct password.
4. Click the Login button.
Expected Result: User is redirected to the dashboard and a welcome message is displayed.
Priority: High
Type: Positive
Linked AC: AC1
(This example only shows the structure and level of detail. It is from a different feature. Do not reuse its content.)

USER STORY:
See the attached file.
```