# Test cases for review: ECOM-142 Apply discount coupon at checkout

| Field | Value |
|---|---|
| **Author** | Junior QA engineer |
| **Story** | ECOM-142 (see `1_story_ECOM-142_apply_coupon.md`) |
| **Status** | Draft, pending peer review |
| **Total test cases** | 6 |

---

Test Case ID: TC-001
Title: Coupon test
Precondition: None
Steps:
1. Go to checkout.
2. Enter SAVE10 and click Apply.
3. Check it works.
Expected Result: Coupon works correctly.
Priority: High
Type: Positive

---

Test Case ID: TC-002
Title: Invalid coupon shows error and expired coupon shows error and total stays same
Precondition: User is logged in with items in cart
Steps:
1. Open the checkout page.
2. Enter coupon code INVALID99 and click Apply.
3. Verify the message "Coupon not found" is displayed.
4. Enter coupon code OLD2023 and click Apply.
5. Verify the message "This coupon has expired" is displayed.
Expected Result: Error messages are displayed and the total does not change.
Priority: Medium
Type: Negative

---

Test Case ID: TC-003
Title: Apply SAVE10 on a cart below the minimum order value
Precondition: User is logged in with a cart subtotal of $30.00
Steps:
1. Open the checkout page.
2. Enter SAVE10 and click Apply.
Expected Result: The message "Minimum order value of $50.00 required" is displayed.
Priority: Low
Type: Negative

---

Test Case ID: TC-004
Title: Apply two coupons together
Precondition: User is logged in with a cart subtotal of $100.00
Steps:
1. Open the checkout page.
2. Apply SAVE10.
3. Apply WELCOME5.
Expected Result: Both discounts are applied and the total is $85.50.
Priority: High
Type: Positive

---

Test Case ID: TC-005
Title: Apply SAVE10 with a valid cart
Precondition: User is logged in with a cart subtotal of $100.00
Steps:
1. Open the checkout page.
2. Enter SAVE10 and click Apply.
Expected Result: A discount of $10.00 is deducted and the total is $90.00. The order summary shows the coupon code, the discount amount and the updated total.
Priority: High
Type: Positive

---

Test Case ID: TC-006
Title: Apply coupon after the test environment database is reset by the TC-005 run
Precondition: TC-005 has been executed successfully
Steps:
1. Enter save10 in lowercase and click Apply.
2. Open the Promotions service admin and confirm the coupon usage count is 2.
Expected Result: Coupon is applied.
Priority: Medium
Type: Edge Case
