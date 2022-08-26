package com.motionbridge.motionbridge.finder.Instagram.application.port;

import com.motionbridge.motionbridge.finder.Instagram.model.Album;

import java.util.List;

public interface InstagramUseCase {
    List<Album> getUserAlbum(String profileName);
}
