package com.motionbridge.motionbridge.finder.instagramv2.web;

import com.motionbridge.motionbridge.finder.Instagram.model.Album;
import com.motionbridge.motionbridge.finder.instagramv2.application.InstagramServiceV2;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/instagram-v2")
public class InstagramControllerV2 {

    private InstagramServiceV2 instagramService;

    @SneakyThrows
    @GetMapping("/{profile}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Album> getContent(@PathVariable String profile) {
//        List<Album>
        final Album album = instagramService.loginInsta(profile);
        return ResponseEntity.ok(album);
    }
}
