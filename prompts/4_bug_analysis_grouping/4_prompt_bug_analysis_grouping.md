# Prompt 4: Bug Analysis and Grouping With AI

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `4_bug_export_release_2.4.md`
**Technique mix:** Role prompting + one-shot example + clarification step + Chain-of-Thought (evidence per group).

---

## Copy-paste prompt

```text
ROLE:
You are a senior QA lead who runs defect triage for e-commerce web applications. You find duplicates, group defects by probable root cause, and spot patterns that point to systemic problems. You back every conclusion with evidence from the bug text and you separate facts from guesses.

CONTEXT:
- The attached file is an export of 15 open bugs from a regression cycle of Release 2.4.
- The output will be used in the triage meeting, to decide what the development team looks at first.
- You only have the bug text. You do not have code, logs, or the ability to reproduce.

TASK:
1. Group the bugs into groups that likely share one root cause or one defect.
2. Mark duplicates: bugs that describe the same defect (same symptom and same module).
3. For each group give a probable root cause hypothesis and a confidence level (High, Medium, Low).
4. Recommend a triage priority for each group (P1 to P4) with a one-line reason.
5. Identify cross-cutting patterns (for example by module, by environment, by reporter, by symptom).
6. Recommend the top 3 groups to fix first.
Reason step by step for each group, listing the bug IDs and the exact evidence that links them, before you state the conclusion.

CONSTRAINTS:
- Group only on evidence in the bug text. Do not merge bugs only because they share a module.
- Every bug must appear in exactly one group. Bugs with no match go to "Ungrouped".
- A root cause is always a hypothesis. Use the words "probable" or "hypothesis". Never state it as fact.
- Priority must consider severity, number of affected bugs, and whether a workaround exists (reload, retry).
- Do not change the severity given in the export. Note a disagreement in the reason line instead.
- Do not suggest code fixes.
- Do not use outside knowledge about the application.

CLARITY (do this BEFORE analysing):
1. List information missing from the export that would change your grouping or priority (for example, a bug whose scope is unclear).
2. Ask me numbered questions, max 4, one line each, and STOP until I answer.
3. If none, write "No clarifications needed." and continue.
Never fill a gap with your own assumption.

FORMAT:
Section A: Groups, one block per group, fields on separate lines in this order:
Group ID: (G1, G2, ...)
Group name: (max 8 words)
Bugs: (IDs, comma separated)
Duplicates: (pairs of IDs, or "None")
Evidence: (bullets, each quoting text from specific bug IDs)
Probable root cause (hypothesis):
Confidence: (High | Medium | Low)
Triage priority: (P1 | P2 | P3 | P4) - one-line reason
Workaround: (stated in bugs, or "None stated")

Section B: Ungrouped bugs: Bug ID | Why it does not fit a group | Suggested priority.

Section C: Cross-cutting patterns, max 5 bullets, each citing bug IDs.

Section D: Summary table: Group ID | Bugs | Count | Highest severity | Priority.

Section E: Top 3 groups to fix first, with one-line reasons.

EXAMPLE (structure only; different data):
Group ID: G9
Group name: Login button unresponsive on iOS
Bugs: BUG-11, BUG-14
Duplicates: BUG-11, BUG-14
Evidence:
- BUG-11: "Login button does nothing on iPhone 14, Safari 17"
- BUG-14: "Tapping Sign in has no effect on iOS 17"
Probable root cause (hypothesis): Click handler failing on iOS Safari.
Confidence: Medium
Triage priority: P1 - blocks login, no workaround stated
Workaround: None stated

BUG EXPORT:
See the attached file.
```