# Prompt 5: Streamlining Bug Reporting With AI

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model (optional but recommended):**
- `5_raw_tester_notes_double_coupon.md`
- The screenshot `coupon_double_apply.png` (not provided here; skip it or use any sample image)
- Optionally `../1_writing_test_cases/1_story_ECOM-142_apply_coupon.md` for the AC reference

**Technique mix:** Role prompting + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior QA engineer who writes defect reports that developers can act on in one read. Your reports are precise, reproducible, neutral in tone, and contain no guesses presented as facts.

CONTEXT:
- The attached file holds a tester's raw notes, a console snippet, and a team chat extract about one defect found during testing of an e-commerce checkout.
- The report will be logged in JIRA and read by developers, the product owner, and the release manager.
- The notes are informal and incomplete.

TASK:
Convert the raw notes into one complete, JIRA-ready bug report.
Also:
- Suggest a severity and a priority with a one-line justification each.
- List every piece of information that is missing or unconfirmed.
- Mention the related acceptance criterion or requirement if the notes cite one.

CONSTRAINTS:
- Use only facts present in the attached material. Never invent steps, versions, results, or environments.
- Where information is missing, write "Not provided" in that field. Do not guess.
- Keep the Actual Result and Expected Result as separate, observable statements. Use measured values (for example $96.00), not words like "wrong" or "broken".
- Steps must be reproducible by someone who has never seen the issue: numbered, one action per step, with concrete test data.
- Use neutral language. Do not blame a team or person. Remove opinions, jokes, and guesses from the report body.
- Title: max 15 words, formatted as "[Module] Symptom when condition".
- Severity values: Critical, High, Medium, Low. Priority values: P1, P2, P3, P4. Show the definition you used in one line.
- Do not suggest code fixes.

CLARITY (do this BEFORE writing the report):
1. List missing or conflicting information that matters for reproducing or rating the defect.
2. Ask me numbered questions, max 5, one line each, and STOP until I answer.
3. If I answer "unknown", write "Not provided" for that item and continue.
Never fill a gap with your own assumption.

FORMAT:
Write the report with these fields, in this order, one per line (steps and lists on following lines):

Title:
Module / Component:
Environment: (application URL or env name, build number, browser and OS, user role)
Preconditions:
Steps to Reproduce:
Actual Result:
Expected Result:
Reproducibility: (for example 3 of 3 attempts)
Severity: (value plus one-line justification)
Priority: (value plus one-line justification)
Related Requirement:
Evidence: (attachments, logs, network calls, quoted exactly)
Additional Notes: (facts only, including developer comments)
Open Questions / Missing Information: (bullets)

After the report, add one line: "Tester summary:" followed by a single sentence suitable for a Slack message.

EXAMPLE (structure only; different defect):
Title: [Login] Error not shown when password field is left empty
Module / Component: Authentication
Environment: QA, build 3.1.0, Chrome 118 on Windows 11, role Customer
Preconditions: User has a registered account
Steps to Reproduce:
1. Open the login page.
2. Enter a registered email.
3. Leave the password field empty.
4. Click Login.
Actual Result: The page reloads with no message and the Login button is enabled.
Expected Result: The message "Password is required" appears under the password field.
Reproducibility: 3 of 3 attempts
Severity: Medium - validation missing, user can still correct input
Priority: P3 - no workaround needed, low business impact
Related Requirement: AC2 of LOGIN-12
Evidence: Screenshot login_empty.png
Additional Notes: Not provided
Open Questions / Missing Information:
- Behaviour on Firefox not tested

RAW NOTES:
See the attached file.
```