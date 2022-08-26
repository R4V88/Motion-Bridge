package com.motionbridge.motionbridge.finder.Instagram.model;

import lombok.Value;

import java.util.List;

@Value
public class Album {
    List<Photo> photos;

//    @Value
    public static class Photo {
        String photoUrl;
        int likes;
        int comments;

        public Photo(String photoUrl, int likes, int comments) {
            this.photoUrl = photoUrl;
            this.likes = likes;
            this.comments = comments;
        }

        public Photo(String photoUrl) {
            this.photoUrl = photoUrl;
            this.likes = 0;
            this.comments = 0;
        }
    }
}
