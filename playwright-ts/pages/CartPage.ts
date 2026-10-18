import { Locator, Page } from '@playwright/test';

export class CartPage {
  readonly checkoutButton: Locator;

  constructor(private readonly page: Page) {
    this.checkoutButton = page.locator('#checkout');
  }

  async clickCheckout(): Promise<void> {
    await this.checkoutButton.click();
  }
}
