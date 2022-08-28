package com.motionbridge.motionbridge.finder.instagram.application.port;

import com.motionbridge.motionbridge.finder.instagram.model.Album;
import com.motionbridge.motionbridge.finder.instagram.web.InstagramController;
import lombok.SneakyThrows;

public interface InstagramUseCase {
    @SneakyThrows
    Album getList(InstagramController.RequestAlbumCommand requestAlbumCommand, String username);
}
