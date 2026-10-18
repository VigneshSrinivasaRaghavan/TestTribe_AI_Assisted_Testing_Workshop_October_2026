import { Locator, Page } from '@playwright/test';

export class ProductsPage {
  readonly addBackpackToCartButton: Locator;
  readonly addBikeLightToCartButton: Locator;
  readonly cartButton: Locator;

  constructor(private readonly page: Page) {
    this.addBackpackToCartButton = page.locator('#add-to-cart-sauce-labs-backpack');
    this.addBikeLightToCartButton = page.locator('#add-to-cart-sauce-labs-bike-light');
    this.cartButton = page.locator('[data-test="shopping-cart-link"]');
  }

  async addBackpackToCart(): Promise<void> {
    await this.addBackpackToCartButton.click();
  }

  async addBikeLightToCart(): Promise<void> {
    await this.addBikeLightToCartButton.click();
  }

  async openCart(): Promise<void> {
    await this.cartButton.click();
  }
}
