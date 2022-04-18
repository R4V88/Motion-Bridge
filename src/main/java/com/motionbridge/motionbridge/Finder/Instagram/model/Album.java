package com.motionbridge.motionbridge.Finder.Instagram.model;

import lombok.Value;

import java.util.List;

@Value
public class Album {
    List<Photo> photos;

    @Value
    public static class Photo {
        String photoUrl;
        Integer likes;
    }
}
