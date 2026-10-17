# ECOM-142: Apply discount coupon at checkout

| Field | Value |
|---|---|
| **Issue Type** | Story |
| **Project** | ShopEasy Web Store |
| **Priority** | High |
| **Sprint** | Sprint 24 |
| **Component** | Checkout |
| **Reporter** | Product Owner |
| **Labels** | checkout, promotions, web |

## Description

As a **registered shopper**,
I want to **apply a discount coupon code on the checkout page**,
so that **I can reduce the total amount I pay for my order**.

## Business Context

- ShopEasy runs a web application (desktop and mobile browsers).
- Coupons are created by the marketing team and stored in the Promotions service.
- Only logged-in users can reach the checkout page.
- Supported currency: USD only.

## Acceptance Criteria

**AC1 - Apply a valid percentage coupon**
- Given I am on the checkout page with items in my cart
- When I enter a valid, active coupon code (e.g. `SAVE10` = 10% off) and click **Apply**
- Then the discount is deducted from the cart subtotal
- And the order summary shows the coupon code, the discount amount, and the updated total

**AC2 - Reject an invalid coupon**
- When I enter a coupon code that does not exist
- Then the message "Invalid coupon code" is displayed
- And the order total does not change

**AC3 - Reject an expired coupon**
- When I enter a coupon that has passed its expiry date
- Then the message "This coupon has expired" is displayed
- And the order total does not change

**AC4 - Minimum order value**
- Coupon `SAVE10` requires a minimum cart subtotal of $50.00
- When the subtotal is below $50.00
- Then the message "Minimum order value of $50.00 required" is displayed
- And the coupon is not applied

**AC5 - One coupon per order**
- When a coupon is already applied and I enter a different coupon
- Then the new coupon replaces the existing one
- And the order summary shows only the new discount

**AC6 - Remove a coupon**
- When I click **Remove** next to an applied coupon
- Then the discount is removed and the total returns to the original subtotal

**AC7 - Coupon code input**
- The coupon field accepts alphanumeric characters only
- Coupon codes are not case-sensitive

## Out of Scope

- Free-shipping coupons
- Gift cards and loyalty points
- Admin-side coupon creation

## Attachments / References

- Figma: Checkout page v3 (coupon field below the order summary)
- API: `POST /api/v1/cart/coupon` (apply), `DELETE /api/v1/cart/coupon` (remove)
