package com.motionbridge.motionbridge.finder.Instagram.application;

import com.motionbridge.motionbridge.finder.Instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.finder.Instagram.model.Album;
import com.motionbridge.motionbridge.finder.Instagram.model.Album.Photo;
import com.motionbridge.motionbridge.finder.commons.port.ChromeDriverUseCase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstagramService implements InstagramUseCase {

    final ChromeDriverUseCase chromeDriver;

    @SneakyThrows
    @Override
    public List<Album> getUserAlbum(String profileName) {
        List<Album> albums = new ArrayList<>(Collections.emptyList());

        WebDriver driver = chromeDriver.initializeChromeDriver();

        String uri = new URIBuilder()
                .setScheme("http")
                .setHost("instagram.com")
                .setPath("/".concat(profileName))
                .build()
                .toString();

        driver.get(uri);

        acceptCookies(driver);

        albums.add(getProfilePhoto(driver));

        List<Album> generatedAlbum = getUserPhotos(driver);
        albums.addAll(generatedAlbum);

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
        WebElement profilePhotoDiv;
        if (driver.findElements(By.xpath("//div/span/img[@class='_6q-tv']")).size() != 0) {
            profilePhotoDiv = driver.findElement(By.xpath("//div/span/img[@class='_6q-tv']"));
            String profilePhotoUrl = profilePhotoDiv.getAttribute("src");
            photo = new Photo(profilePhotoUrl, "0");
        } else {
            photo = new Photo("no profile photo", "0");
        }

        profilePhoto = new Album(Collections.singletonList(photo));
        return profilePhoto;
    }

    private List<Album> getUserPhotos(WebDriver driver) {
        List<Album> photos = new ArrayList<>(Collections.emptyList());

        List<WebElement> containers = driver.findElements(By.xpath("//div[@class = 'Nnq7C weEfm']"));
        containers.forEach(container -> {
            WebElement image = driver.findElement(By.xpath(".//div/a"));
            String url = image.getAttribute("href");
            driver.get(url);

            driver.manage().timeouts().pageLoadTimeout(Duration.ofMillis(2000));
            WebElement currentImage = driver.findElement(By.xpath("//div/div/img[@Class='FFVAD']"));
            String getUrl = currentImage.getAttribute("src");
            log.warn("Found image url " + getUrl);
            WebElement getLikes = driver.findElement(By.xpath("//a/div/span"));
            String likes = getLikes.getText();
            log.warn("Found image likes " + likes);
            Photo photo = new Photo(getUrl, likes);
            log.warn("Created photo: url -> " + photo.getPhotoUrl() + " photo likes -> " + photo.getLikes());

            photos.add(new Album(List.of(photo)));
            log.warn("New album created");
        });

        return photos;
    }
}
