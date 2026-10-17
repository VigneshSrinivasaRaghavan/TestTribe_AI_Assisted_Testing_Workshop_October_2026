# Release inputs: ShopEasy Checkout Revamp, Sprint 3 (Release 3.0-beta)

This file collects the inputs a QA lead has at hand when writing the test plan for one release. It mixes a formal summary with meeting notes.

---

## 1. Release summary

| Field | Value |
|---|---|
| **Release** | Checkout Revamp Release 3.0-beta |
| **Sprint** | Sprint 3 of 4 (plus a hardening sprint) |
| **Sprint dates** | 01/05/2027 to 01/18/2027 |
| **Code freeze** | 01/15/2027 |
| **Deploy to Staging** | 01/18/2027 |
| **Release type** | Internal beta to 5% of customers |
| **QA Lead** | Divya Menon |
| **Testers** | Kiran (senior, automation), Sana (mid-level), Tom (mid-level) |

## 2. Features in this release

| Story | Feature | Status at planning |
|---|---|---|
| ECOM-142 | Apply discount coupon at checkout | In development |
| ECOM-157 | Enter shipping address at checkout | Done, in QA |
| ECOM-160 | Select delivery date | In development |
| ECOM-171 | Pay by credit or debit card | In development, depends on gateway sandbox keys |
| ECOM-172 | Pay with PayPal | Not started, may slip to Sprint 4 |
| ECOM-180 | Order confirmation page and email | In development |
| ECOM-185 | Guest checkout | Not started |

## 3. Out of scope for this release

- International shipping
- Gift cards and loyalty points
- Performance and load testing (planned for the hardening sprint)

## 4. Environments

- QA: stable, shared with the Payments team. Deployed on every merge to main.
- Staging: receives the release candidate on 01/18/2027. Connected to the gateway sandbox.
- Browsers for this release: Chrome and Safari (latest) on desktop, Android Chrome and iOS Safari. Firefox and Edge are deferred to Sprint 4.

## 5. Dependencies

- Gateway sandbox API keys (requested on 12/20/2026, not received yet).
- Promotions team must publish 4 test coupons in QA by 01/07/2027.
- Designer provides final mobile screens for delivery date by 01/08/2027.

## 6. Team capacity (from sprint planning)

- Sprint length: 10 working days (01/05 to 01/18, weekends excluded)
- Kiran: 100% available.
- Sana: 80% (one day on leave, 01/12).
- Tom: 100% for 7 days, then moved to support the Payments team release from 01/14 (unconfirmed).

## 7. Meeting notes (QA kick-off, 01/04/2027)

- Regression suite: 60 Selenium tests, 20% flaky. Kiran will try to stabilise 10 of them this sprint.
- Existing manual regression of 420 cases is outdated. Sana will mark which ones still apply for checkout, not full cleanup.
- API tests for the Cart and Promotions services: Kiran proposed starting this sprint, not yet agreed.
- Bugs go into JIRA project ECOM, triaged daily at 10:00.
- Test management tool: Zephyr Scale was chosen last week.
- Exit criteria were discussed but not finalised: no open Critical or High bugs seems agreed. The rule for Medium bugs is open.
- Product owner wants a daily one-line status in Slack.
