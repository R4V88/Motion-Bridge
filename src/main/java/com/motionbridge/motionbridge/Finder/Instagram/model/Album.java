package com.motionbridge.motionbridge.Finder.Instagram.model;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

import java.util.List;

@Value
@RequiredArgsConstructor
public class Album {
    List<Photo> photos;

    @Value
    public static class Photo {
        String photoUrl;
        String likes;
    }
}
