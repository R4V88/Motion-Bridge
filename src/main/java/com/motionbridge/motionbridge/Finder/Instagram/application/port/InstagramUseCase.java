package com.motionbridge.motionbridge.Finder.Instagram.application.port;

import com.motionbridge.motionbridge.Finder.Instagram.model.Album;

import java.util.List;

public interface InstagramUseCase {
    List<Album> getUserPhotos(String userId);
}
