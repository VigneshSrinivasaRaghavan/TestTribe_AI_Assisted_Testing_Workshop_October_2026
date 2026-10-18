# SD-101: Log in to SauceDemo

| Field | Value |
|---|---|
| **Issue Type** | Story |
| **Project** | SauceDemo Web Store |
| **Priority** | High |
| **Component** | Authentication |
| **URL** | https://www.saucedemo.com |

## Description

As a **shopper**,
I want to **log in with my username and password**,
so that **I can browse and buy products**.

## Test Users (public, provided by SauceDemo)

| Username | Password | Behaviour |
|---|---|---|
| standard_user | secret_sauce | Normal user |
| locked_out_user | secret_sauce | Account is locked |

## Acceptance Criteria

**AC1 - Valid login**
- When I enter `standard_user` / `secret_sauce` and click **Login**
- Then I land on `/inventory.html` and the page title "Products" is displayed

**AC2 - Wrong password**
- When I enter `standard_user` with a wrong password
- Then the message "Epic sadface: Username and password do not match any user in our service" is displayed
- And I stay on the login page

**AC3 - Locked-out user**
- When I enter `locked_out_user` / `secret_sauce`
- Then the message "Epic sadface: Sorry, this user has been locked out." is displayed

**AC4 - Empty username**
- When I leave Username empty and click **Login**
- Then the message "Epic sadface: Username is required" is displayed

**AC5 - Empty password**
- When I enter a username, leave Password empty, and click **Login**
- Then the message "Epic sadface: Password is required" is displayed

**AC6 - Dismiss error**
- When an error message is displayed and I click its close (X) button
- Then the error message disappears

**AC7 - Logout**
- When I am logged in and choose **Logout** from the menu
- Then I return to the login page

**AC8 - Protected page**
- When I open `/inventory.html` without logging in
- Then the message "Epic sadface: You can only access '/inventory.html' when you are logged in." is displayed on the login page

## Out of Scope

- Other SauceDemo users (problem_user, performance_glitch_user, error_user, visual_user)
- Cart, checkout, and product pages
