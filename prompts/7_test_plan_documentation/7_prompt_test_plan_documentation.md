# Prompt 7: Documenting Test Plans Using AI Tools

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `7_release_inputs_sprint_3_test_plan.md`
Optional: `../6_test_strategy/6_project_brief_checkout_revamp.md` (shows how the plan follows the strategy).

**Technique mix:** Role prompting + one-shot example + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a QA lead who writes release-level test plans for agile e-commerce teams. Your plans are concise, specific to the release, and usable on day one by testers and by the release manager. You never present a gap as a fact.

CONTEXT:
- The attached file contains the release summary, features, environments, dependencies, team capacity and raw QA kick-off meeting notes for one sprint release.
- A test plan is release-specific: it states what will be tested, by whom, when, in which environment, and what "done" means. The project-level approach (strategy) is only referenced, not repeated.
- The plan will be shared with the product owner, the engineering manager, and the release manager, and stored in Confluence.

TASK:
Write the test plan for this release with these sections:
1. Introduction and objective (max 3 sentences)
2. Scope: features to test (with story IDs and a risk-based priority High, Medium, Low) and features not to test
3. Test approach per feature group (types of testing, manual vs automated)
4. Test environments and browsers
5. Test data and tools
6. Schedule and milestones (derived only from dates in the attachment)
7. Resources and capacity (per person, with available days)
8. Entry criteria and exit (release) criteria
9. Risks, dependencies and mitigations
10. Deliverables and reporting (including defect triage and status updates)
11. Open items and decisions needed (each with a suggested owner)

CONSTRAINTS:
- Use only facts from the attachment. Do not invent dates, people, tools, environments, numbers, or agreements.
- Mark anything unconfirmed or undecided in the notes as "TBC" and list it in section 11. Do not treat a proposal as an agreement.
- Check the capacity against the scope. If the work does not fit the available days, say so in section 9 with a trade-off, but do not drop scope by yourself.
- Scope must follow the attachment: items marked out of scope stay out of scope.
- Use concise language. Every bullet is one sentence. No generic text that could fit any release.
- Do not write test cases and do not copy the strategy text.
- Maximum 3 pages (about 1,200 words) excluding tables.

CLARITY (do this BEFORE writing):
1. List gaps and contradictions in the attachment that affect the plan (for example, unconfirmed dependencies, capacity conflicts, unclear exit criteria).
2. Ask me numbered questions, max 6, one line each, and STOP until I answer.
3. If I answer "unknown" or "TBC", carry that status into the plan. Never resolve a gap with your own assumption.

FORMAT:
Use the section numbers and titles above as headings, in that order.
- Section 2: table with columns Story ID | Feature | Priority | Status | Notes.
- Section 6: table with columns Milestone | Date | Owner. Use "TBC" if the date is not in the attachment.
- Section 7: table with columns Name | Role | Availability | Focus area.
- Section 8: two bullet lists, "Entry criteria" and "Exit criteria". Each criterion must be measurable.
- Section 9: table with columns Risk | Impact | Mitigation | Owner.
- All other sections: bullets.
- Dates written as MM/DD/YYYY. Use "TBC" for unknown values.
- Begin the document with a small header table: Plan title, Release, Version, Author (QA Lead), Status (Draft).

EXAMPLE (structure only; different release):
Section 2 row:
| PAY-12 | Save card for future use | High | In development | Depends on gateway tokenisation API |

Section 9 row:
| Gateway sandbox keys not received | Card payment cannot be tested in Staging | Escalate to the vendor by 01/08/2027. Use mock service in QA meanwhile. | Engineering manager |

RELEASE INPUTS:
See the attached file.
```