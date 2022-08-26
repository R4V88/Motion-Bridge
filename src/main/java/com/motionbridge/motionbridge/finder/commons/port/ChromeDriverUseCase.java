package com.motionbridge.motionbridge.finder.commons.port;

import org.openqa.selenium.WebDriver;

public interface ChromeDriverUseCase {
    WebDriver initializeChromeDriver();
    void shutDownDriver(WebDriver driver);
}
