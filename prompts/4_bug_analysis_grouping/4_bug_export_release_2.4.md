# Bug export: ShopEasy Web Store, Release 2.4 regression cycle

| Field | Value |
|---|---|
| **Source** | JIRA filter: project = ECOM AND fixVersion = 2.4 AND status = Open |
| **Export date** | 10/16/2026 |
| **Total bugs** | 15 |
| **Environment (all)** | QA environment, build 2.4.0-rc2 unless stated |

| Bug ID | Title | Description | Module | Severity | Reporter | Created |
|---|---|---|---|---|---|---|
| ECOM-301 | Order total shows $0.00 after applying coupon | Applied SAVE10 on a $100 cart. Total changed to $0.00. Refreshing the page shows the correct total of $90.00. | Checkout | Critical | Anita | 10/12 |
| ECOM-302 | Cart total wrong after coupon on mobile | Chrome on Android. Applied SAVE10, order summary total displayed as $0.00 until reload. | Checkout | High | Ravi | 10/12 |
| ECOM-303 | Payment page timeout after 30 seconds | Clicking Pay with a valid test card spins for 30 seconds then shows "Request timed out". Happens about 1 in 4 attempts. Console shows 504 from /api/v1/payments. | Payment | Critical | Meera | 10/13 |
| ECOM-304 | 504 gateway error on Pay button | Intermittent 504 when paying with a saved card. Retry sometimes works. | Payment | High | Anita | 10/13 |
| ECOM-305 | Order confirmation email not received | Placed order ORD-5521. No confirmation email after 15 minutes. Spam folder checked. | Notifications | Medium | Ravi | 10/13 |
| ECOM-306 | Confirmation email missing for guest orders | Guest checkout order ORD-5530 did not trigger an email. Logged-in orders send email fine. | Notifications | Medium | Meera | 10/14 |
| ECOM-307 | Search returns no results for products with apostrophe | Searching "men's shoes" returns 0 results. "mens shoes" returns 48. | Search | Medium | Anita | 10/14 |
| ECOM-308 | Search fails for brand "L'Oreal" | Searching L'Oreal shows "No products found" while products exist in the catalogue. | Search | Medium | Ravi | 10/14 |
| ECOM-309 | Product images blurry on retina display | Product detail images look pixelated on MacBook Pro Retina in Safari. | Catalogue | Low | Meera | 10/14 |
| ECOM-310 | Add to cart button misaligned on product page | On screens narrower than 360px the button overlaps the price. | Catalogue | Low | Anita | 10/15 |
| ECOM-311 | Cannot remove coupon after applying | Clicking Remove next to SAVE10 does nothing. Total stays discounted. Console error: TypeError undefined reading couponId. | Checkout | High | Ravi | 10/15 |
| ECOM-312 | Order total shows $0.00 after applying coupon | Same as ECOM-301. Seen on Firefox desktop. Reload fixes it. | Checkout | High | Meera | 10/15 |
| ECOM-313 | Slow payment response | Pay button takes about 8 seconds on the staging Pay flow. Works eventually. | Payment | Low | Ravi | 10/15 |
| ECOM-314 | Login session expires after 5 minutes of inactivity | Session ends at 5 minutes. Requirement says 30 minutes. | Authentication | High | Anita | 10/16 |
| ECOM-315 | Order history page shows wrong date format | Dates show as 2026-10-12 instead of MM/DD/YYYY. | Account | Low | Meera | 10/16 |
