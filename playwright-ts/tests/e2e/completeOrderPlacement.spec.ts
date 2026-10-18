import { test, expect } from '@playwright/test';
import { LoginPage } from '../../pages/LoginPage';
import { ProductsPage } from '../../pages/ProductsPage';
import { CartPage } from '../../pages/CartPage';
import { CheckoutInformationPage } from '../../pages/CheckoutInformationPage';
import { CheckoutOverviewPage } from '../../pages/CheckoutOverviewPage';
import { CheckoutCompletePage } from '../../pages/CheckoutCompletePage';
import credentials from '../../test-data/credentials.json';

for (const credential of credentials) {
  test(`complete order placement for backpack and bike light [${credential.id}: ${credential.username}]`, async ({ page }) => {
    const loginPage = new LoginPage(page);
    const productsPage = new ProductsPage(page);
    const cartPage = new CartPage(page);
    const checkoutInformationPage = new CheckoutInformationPage(page);
    const checkoutOverviewPage = new CheckoutOverviewPage(page);
    const checkoutCompletePage = new CheckoutCompletePage(page);

    const suffix = Date.now().toString();

    await loginPage.goto();
    await loginPage.enterUsername(credential.username);
    await loginPage.enterPassword(credential.password);
    await loginPage.clickLogin();

    await productsPage.addBackpackToCart();
    await productsPage.addBikeLightToCart();
    await productsPage.openCart();

    await cartPage.clickCheckout();

    await checkoutInformationPage.enterFirstName(`First${suffix}`);
    await checkoutInformationPage.enterLastName(`Last${suffix}`);
    await checkoutInformationPage.enterPostalCode(`${Math.floor(Math.random() * 90000) + 10000}`);
    await checkoutInformationPage.clickContinue();

    await checkoutOverviewPage.clickFinish();

    await expect(checkoutCompletePage.completeHeader).toHaveText('Thank you for your order!');
  });
}
