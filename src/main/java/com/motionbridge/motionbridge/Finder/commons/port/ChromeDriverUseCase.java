package com.motionbridge.motionbridge.Finder.commons.port;

import org.openqa.selenium.WebDriver;

public interface ChromeDriverUseCase {
    WebDriver initializeChromeDriver();
    void shutDownDriver(WebDriver driver);
}
