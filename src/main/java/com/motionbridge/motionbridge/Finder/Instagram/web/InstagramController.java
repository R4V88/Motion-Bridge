package com.motionbridge.motionbridge.Finder.Instagram.web;

import com.motionbridge.motionbridge.Finder.Instagram.application.port.InstagramUseCase;
import com.motionbridge.motionbridge.Finder.Instagram.model.Album;
import lombok.AllArgsConstructor;
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.FOUND)
    public List<Album> getContent(@PathVariable String id) {
        return instagram.getUserPhotos(id);
    }
}
