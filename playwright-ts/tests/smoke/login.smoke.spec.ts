import { test, expect } from '@playwright/test';

const USERNAME = 'standard_user';
const PASSWORD = 'secret_sauce';

test('Log in with valid credentials and land on products', async ({ page }) => {
  await page.goto('/');

  await page.locator('#user-name').fill(USERNAME);
  await page.locator('#password').fill(PASSWORD);
  await page.locator('#login-button').click();

  await expect(page).toHaveURL(/\/inventory\.html/);
  await expect(page.locator('[data-test="title"]')).toHaveText('Products');
});
