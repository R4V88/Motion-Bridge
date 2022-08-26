package com.motionbridge.motionbridge.finder.Instagram.web;

import com.motionbridge.motionbridge.finder.Instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.finder.Instagram.model.Album;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/instagram")
public class InstagramController {

    private InstagramUseCase instagram;

    @SneakyThrows
    @GetMapping("/{profile}")
    @ResponseStatus(HttpStatus.OK)
    public List<Album> getContent(@PathVariable String profile) {
        return instagram.getUserAlbum(profile);
        //TODO added https://github.com/postaddictme/instagram-java-scraper to dependency
    }
}
