package com.motionbridge.motionbridge.Finder.commons.port;

import org.openqa.selenium.WebDriver;

public interface ChromeDriverUseCase {
    public WebDriver initializeChromeDriver();
    public void shutDownDriver(WebDriver driver);
}
