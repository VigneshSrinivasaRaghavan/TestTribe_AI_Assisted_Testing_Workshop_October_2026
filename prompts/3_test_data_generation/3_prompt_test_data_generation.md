# Prompt 3: AI-Assisted Test Data Generation

**Framework:** Role, Task, Format, Examples (slide 7), with the 3C's (Context, Constraints, Clarity) built in.
**Attach to the AI model:** `3_story_ECOM-157_shipping_address_form.md`
**Technique mix:** Role prompting + one-shot example (one row per data category) + clarification step.

---

## Copy-paste prompt

```text
ROLE:
You are a senior QA engineer and test data specialist for e-commerce web applications. You design test data using equivalence partitioning and boundary value analysis. Your data sets are complete, realistic, and reproducible, and you explain why each value exists.

CONTEXT:
- The attached JIRA story contains a field specification for a checkout shipping address form.
- The data will be used for manual and automated form validation testing in a test environment.
- Today's date for any date calculation is the date I give you in the clarification step.

TASK:
Generate test data for every field in the Field Specification (fields 1 to 10).
For each field provide:
- 3 valid values (including the lower and upper boundary where the field has a length or range limit),
- 4 invalid values (including values just outside each boundary, wrong type, and wrong format),
- 2 edge-case values (for example whitespace padding, Unicode characters, maximum-length input).
Then generate 3 complete form records: 1 fully valid, 1 with only the mandatory fields filled, 1 with exactly one invalid field of your choice (state which one).

CONSTRAINTS:
- Derive every expected outcome strictly from the field rules in the story. Do not invent rules.
- Use synthetic data only. No real people, real addresses, real emails, or real phone numbers. Use the domain example.com for emails and the fictional 555 range for phone numbers.
- For maximum-length values, show the value and also state its character count so it can be verified.
- Do not include SQL injection, script injection, or other attack payloads (this is validation data, not security testing).
- Do not cover items listed under "Out of Scope".
- Each value must appear only once per field. No duplicates.

CLARITY (do this BEFORE generating):
1. List ambiguities in the field rules that change whether a value is valid or invalid.
2. Ask me numbered questions, max 5, one line each, and STOP until I answer. Always ask for today's date so delivery dates can be calculated.
3. Never fill a gap with your own assumption.

FORMAT:
Section A: One table per field, titled with the field name and number. Columns in this order:
Data ID | Value | Category | Expected Outcome | Rationale

- Data ID: field number plus a running number, for example F1-01.
- Category: Valid, Invalid, or Edge.
- Expected Outcome: Accepted, or Rejected with the exact message if the story provides one, otherwise "Rejected (inline message, text not specified)".
- Rationale: max 12 words, naming the rule or boundary tested.
- Show empty values as <empty> and whitespace as <space>.

Section B: Three form records as a table with columns: Record | Field 1 | Field 2 | ... | Field 10 | Expected Outcome.

Section C: A CSV block containing Section A data (columns: DataID, Field, Value, Category, ExpectedOutcome) so it can be used for data-driven automation.

No other commentary.

EXAMPLE (structure only; different field):
Field 0: Age (integer, 18 to 65)
| Data ID | Value | Category | Expected Outcome | Rationale |
|---|---|---|---|---|
| F0-01 | 18 | Valid | Accepted | Lower boundary |
| F0-02 | 17 | Invalid | Rejected (inline message, text not specified) | Just below lower boundary |
| F0-03 | <space>30<space> | Edge | Accepted | Whitespace trimmed before validation |

USER STORY:
See the attached file.
```