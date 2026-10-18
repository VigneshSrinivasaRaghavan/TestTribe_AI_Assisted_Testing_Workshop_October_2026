import { Locator, Page } from '@playwright/test';

export class CheckoutCompletePage {
  readonly completeHeader: Locator;

  constructor(private readonly page: Page) {
    this.completeHeader = page.locator('[data-test="complete-header"]');
  }
}
