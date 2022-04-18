package com.motionbridge.motionbridge.Finder.commons;

import com.motionbridge.motionbridge.Finder.commons.port.ChromeDriverUseCase;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

@Service
public class ChromeDriverManipulation implements ChromeDriverUseCase {

    @Override
    public WebDriver initializeChromeDriver() {
        WebDriverManager.chromedriver().setup();
        /* Uncomment lines below to run chrome driver without opening browser */
//        ChromeOptions opt = new ChromeOptions();
//        opt.addArguments("headless");
//        return new ChromeDriver(opt);
        return new ChromeDriver();
    }

    @Override
    public void shutDownDriver(WebDriver driver) {
        driver.quit();
    }
}


