package com.motionbridge.motionbridge.Finder.instagramv2.web;

import com.github.instagram4j.instagram4j.IGClient;
import com.motionbridge.motionbridge.Finder.instagramv2.application.InstagramServiceV2;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/instagram-v2")
public class InstagramControllerV2 {

    private InstagramServiceV2 instagramService;

    @SneakyThrows
    @GetMapping("/{profile}")
    @ResponseStatus(HttpStatus.OK)
    public void getContent(@PathVariable String profile) {
//        List<Album>
        instagramService.loginInsta(profile);
    }
}
