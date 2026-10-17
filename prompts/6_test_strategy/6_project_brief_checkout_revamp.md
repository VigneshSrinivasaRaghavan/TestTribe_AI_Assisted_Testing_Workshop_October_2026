# Project brief: ShopEasy Checkout Revamp

| Field | Value |
|---|---|
| **Project** | ShopEasy Checkout Revamp |
| **Product Owner** | Neha Sharma |
| **Engineering Manager** | Arjun Rao |
| **QA Lead** | (to be assigned) |
| **Document status** | Approved for planning, v1.0 |

## 1. Background

ShopEasy is an online retail store serving US customers. The current checkout is a 5-step flow built 6 years ago. Cart abandonment at checkout is 68%. The business wants a faster, simpler 3-step checkout to lower abandonment.

## 2. Objectives

- Reduce checkout steps from 5 to 3 (Cart review, Shipping and delivery, Payment and confirmation).
- Reduce cart abandonment from 68% to 55% within 3 months after launch.
- Keep the payment failure rate at or below the current 1.5%.

## 3. Scope

**In scope**
- New 3-step checkout UI (responsive web, desktop and mobile browsers)
- Shipping address form and delivery date selection
- Coupon code application and removal
- Payment by credit/debit card and PayPal (through an external payment gateway)
- Order confirmation page and confirmation email
- Order history display of the new orders
- Guest checkout and logged-in checkout

**Out of scope**
- Native mobile apps
- International shipping and multi-currency
- Gift cards and loyalty points
- Changes to the product catalogue and search

## 4. Technical overview

- Front end: React single-page application.
- Back end: Java microservices (Cart, Promotions, Orders, Notifications, Users).
- Payment gateway: third-party (card tokenisation hosted by the gateway). PayPal through its SDK.
- Database: PostgreSQL.
- Hosting: AWS, 3 environments (Dev, QA, Staging), then Production.
- CI/CD: GitHub Actions. Deployment to QA on every merge to main.
- Existing test assets: 420 manual regression test cases (Excel, partly outdated), 60 Selenium UI tests (Java, flaky about 20%), no API automation, no performance tests.

## 5. Team and timeline

- Development: 8 developers (5 back end, 3 front end).
- QA: 3 testers (1 senior, 2 mid-level). One has automation skills in Selenium and Java. Nobody has performance testing experience.
- Product and design: 1 product owner, 1 designer.
- Delivery: 4 two-week sprints, then 1 hardening sprint.
- Go-live target: 02/16/2027 (fixed, tied to a marketing campaign).

## 6. Quality requirements and compliance

- PCI-DSS: the application must never store or log full card numbers (handled by gateway tokenisation).
- Page load: checkout pages must load in under 3 seconds on 4G.
- Availability: 99.9% during campaign periods.
- Accessibility: WCAG 2.1 AA is a "should have", not mandatory for this release.
- Browsers: latest two versions of Chrome, Safari, Firefox, and Edge; iOS Safari and Android Chrome.

## 7. Known risks and concerns (from kick-off meeting)

- The payment gateway sandbox is sometimes unstable and has rate limits.
- Promotions service rules are complex (percentage, fixed amount, minimum order, expiry) and owned by another team.
- The go-live date is fixed, and the scope may not shrink.
- The Selenium suite is unreliable, and the manual regression is outdated.
- Test data in QA is shared with other teams and gets overwritten.
- Black Friday-level load is expected during the campaign (about 5x normal traffic).

## 8. Open items

- Test management tool is not decided (Zephyr Scale or Xray).
- Whether staging has production-like data volume is not confirmed.
