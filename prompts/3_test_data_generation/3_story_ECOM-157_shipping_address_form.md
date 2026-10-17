# ECOM-157: Enter shipping address at checkout

| Field | Value |
|---|---|
| **Issue Type** | Story |
| **Project** | ShopEasy Web Store |
| **Priority** | High |
| **Sprint** | Sprint 24 |
| **Component** | Checkout |
| **Labels** | checkout, address, forms, web |

## Description

As a **registered shopper**,
I want to **enter my shipping address on the checkout page**,
so that **my order is delivered to the right place**.

## Business Context

- Shipping is available to the United States only.
- Address data is validated on the client and again on the server.
- Test environments must never contain real customer data.

## Field Specification

| # | Field | Mandatory | Type | Rules |
|---|---|---|---|---|
| 1 | Full name | Yes | Text | 2 to 50 characters. Letters, spaces, hyphens, apostrophes and full stops allowed. No digits. |
| 2 | Email | Yes | Email | Standard email format. Max 254 characters. |
| 3 | Phone number | Yes | Text | US number, exactly 10 digits. Spaces, hyphens and brackets are accepted and removed before validation. |
| 4 | Address line 1 | Yes | Text | 5 to 100 characters. Letters, digits, spaces, commas, hyphens, full stops and # allowed. |
| 5 | Address line 2 | No | Text | Up to 100 characters. Same allowed characters as line 1. |
| 6 | City | Yes | Text | 2 to 40 characters. Letters, spaces and hyphens only. |
| 7 | State | Yes | Dropdown | One of the 50 US state codes (for example CA, NY, TX). |
| 8 | ZIP code | Yes | Text | 5 digits (12345) or ZIP+4 (12345-6789). |
| 9 | Delivery date | Yes | Date | Format MM/DD/YYYY. Earliest: 2 days from today. Latest: 30 days from today. |
| 10 | Order quantity | Yes | Integer | 1 to 10. |

## Acceptance Criteria

**AC1** - With all mandatory fields valid, the **Continue to payment** button is enabled and the user moves to the payment page.

**AC2** - If any field is invalid, an inline message appears under that field and **Continue to payment** stays disabled.

**AC3** - Leading and trailing spaces are trimmed from all text fields before validation.

**AC4** - Mandatory fields left empty show the message "This field is required".

## Out of Scope

- International addresses
- Address autocomplete
- Payment details
