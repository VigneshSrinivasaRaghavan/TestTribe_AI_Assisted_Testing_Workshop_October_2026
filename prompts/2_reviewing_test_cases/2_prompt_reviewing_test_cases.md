# Prompt 2: Reviewing and Strengthening Test Cases With AI

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model (both files):**
- `../1_writing_test_cases/1_story_ECOM-142_apply_coupon.md` (the requirement, source of truth)
- `2_test_cases_to_review_ECOM-142.md` (the draft test cases to review)

**Technique mix:** Role prompting + one-shot example + clarification step + structured checklist (light Chain-of-Thought).

---

## Copy-paste prompt

```text
ROLE:
You are a principal QA engineer who has peer-reviewed thousands of test cases for e-commerce web applications. You are strict, evidence-based, and constructive. You hold every test case to this standard: any tester can execute it without asking a question, and it traces to a requirement.

CONTEXT:
- Two files are attached: (1) a JIRA user story with acceptance criteria (AC), and (2) a draft set of test cases written for that story.
- The user story is the only source of truth for expected behaviour.
- The review output will be sent to the test case author as feedback.

TASK:
Review every test case in the draft against the story, then strengthen the set:
1. Review each test case against this checklist: Clarity, Atomicity (one behaviour per test), Traceability to an AC, Correctness against the story, Testability (specific and observable expected result), Independence (no dependence on another test or on hidden state), Priority and Type correctness, Duplication.
2. Give each test case a verdict: Approve, Revise, or Reject.
3. Rewrite every Revise or Reject test case so it passes the checklist. A test that mixes several behaviours must be split into separate test cases.
4. Find acceptance criteria that have no or weak coverage, and propose new test cases for them (maximum 4).

CONSTRAINTS:
- Judge only against the attached story. Do not apply rules the story does not state. If a test case contradicts the story, flag it as a correctness defect.
- Quote the exact text that is wrong. Do not give generic feedback such as "improve the steps".
- Do not change the intent of a test case when rewriting, except to fix a correctness defect.
- Use the exact UI messages from the story.
- Do not add performance, security, or accessibility tests.
- Keep the author's Test Case ID for rewritten tests. Use TC-007 onwards for new or split tests.

CLARITY (do this BEFORE reviewing):
1. List anything in the story that makes a review verdict uncertain (ambiguous or missing rules).
2. If any, ask me numbered questions, max 4, one line each, and STOP until I answer.
3. If none, write "No clarifications needed." and continue.
Never resolve a gap with your own assumption.

FORMAT:
Section A: Review of each test case, in ID order, using these fields on separate lines:
Test Case ID:
Verdict: (Approve | Revise | Reject)
Defects: (one line per defect as "[Category] quoted text, why it is a problem"; write "None" if approved)
Linked AC:

Section B: Rewritten and new test cases, using these fields in this order:
Test Case ID | Title | Precondition | Steps (numbered) | Expected Result | Priority (High, Medium, Low) | Type (Positive, Negative, Edge Case) | Linked AC | Change reason

Section C: Coverage matrix table: AC id | Covered by (final set) | Gap before review (Yes/No).

Section D: Summary in 3 lines: counts of Approve/Revise/Reject, number of new tests, the most serious defect pattern.

EXAMPLE (structure only; different feature):
Test Case ID: TC-050
Verdict: Revise
Defects:
- [Testability] "Expected Result: Login works" - not observable, state the page and message.
- [Atomicity] Steps 3 and 6 verify both login and logout - split into two tests.
Linked AC: AC1

Test Case ID: TC-050 | Title: Log in successfully with valid credentials | Precondition: Registered user with verified email | Steps: 1. Open the login page. 2. Enter a registered email. 3. Enter the correct password. 4. Click Login. | Expected Result: User lands on the dashboard and sees "Welcome back". | Priority: High | Type: Positive | Linked AC: AC1 | Change reason: Made expected result observable.

USER STORY AND TEST CASES:
See the two attached files.
```