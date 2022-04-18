package com.motionbridge.motionbridge.Finder.Instagram.application;

import com.motionbridge.motionbridge.Finder.Instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.Finder.Instagram.model.Album;
import com.motionbridge.motionbridge.Finder.Instagram.model.Album.Photo;
import com.motionbridge.motionbridge.Finder.commons.port.ChromeDriverUseCase;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class InstagramService implements InstagramUseCase {

    ChromeDriverUseCase chromeDriver;

    @SneakyThrows
    @Override
    public List<Album> getUserPhotos(String userId) {
        WebDriver driver = chromeDriver.initializeChromeDriver();
        String uri = new URIBuilder()
                .setScheme("http")
                .setHost("instagram.com")
                .setPath("/".concat(userId))
                .build()
                .toString();

        driver.get(uri);
        log.warn("Adres uzytkownika: " + driver.getCurrentUrl());
        acceptCookies(driver);
        List<Album> albums = new ArrayList<>(Collections.emptyList());
        albums.add(getProfilePhoto(driver));


        chromeDriver.shutDownDriver(driver);

        return albums;
    }

    private void acceptCookies(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(3600));
        WebElement acceptCookies = driver.findElement(By.xpath("/html/body/div[4]/div/div/button[1]"));
        acceptCookies.click();
    }

    private Album getProfilePhoto(WebDriver driver) {
        driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(3600));
        Album profilePhoto;
        Photo photo;
        WebElement profilePhotoDiv = driver.findElement(By.xpath("//div/span/img[@class='_6q-tv']"));
        String profilePhotoUrl = profilePhotoDiv.getAttribute("src");
        photo = new Photo(profilePhotoUrl, 0);
        profilePhoto = new Album(Collections.singletonList(photo));
        return profilePhoto;
    }

    private Album getUserPhotos(WebDriver driver) {
        Album photos;

        return null;
    }
}
