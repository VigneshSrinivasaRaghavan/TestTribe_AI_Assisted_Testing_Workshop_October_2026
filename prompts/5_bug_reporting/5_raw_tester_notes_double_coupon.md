# Raw tester notes: coupon discount applied twice

These are the unedited notes of a tester during exploratory testing. They are incomplete and messy on purpose.

---

## Session notes (Ravi, 10/16/2026, around 3pm)

testing coupon stuff on checkout, ECOM-142 feature. found something weird. i think discount is applied 2 times??

cart had 2 items, subtotal was 120.00. entered SAVE10 and clicked apply really fast twice (double click by mistake). total went to 96.00 instead of 108.00. so 10 + 10 = 20 off? wait 12+12 = 24 off. yes 96.

tried again slowly, single click -> 108.00 correct. so only on double click. tried 3 times with double click, happened 3 of 3. 

then i removed coupon, total back to 120.00 fine.

not sure about mobile, didnt try. firefox also not tried.

chrome latest on my mac. QA env. logged in as ravi.test@example.com. build is 2.4.0-rc2 i think

screenshot saved: coupon_double_apply.png (shows 96.00 and the order summary with SAVE10 -24.00)

network tab: two POST calls to /api/v1/cart/coupon, both returned 200. didn't check response body

this is bad, customers lose money for us. should be critical imo? or maybe not because nobody double clicks. idk

the story says one coupon per order so this breaks AC5 i guess

---

## Console snippet pasted from DevTools

```text
POST https://qa.shopeasy.example.com/api/v1/cart/coupon 200 (OK)  142ms
POST https://qa.shopeasy.example.com/api/v1/cart/coupon 200 (OK)  151ms
[CartSummary] total updated: 96.00
```

---

## Other info from the team chat

- Dev (Priya): "apply endpoint should be idempotent, will check"
- Release is planned for 10/23/2026
