import { Locator, Page } from '@playwright/test';

export class CheckoutOverviewPage {
  readonly finishButton: Locator;

  constructor(private readonly page: Page) {
    this.finishButton = page.locator('#finish');
  }

  async clickFinish(): Promise<void> {
    await this.finishButton.click();
  }
}
