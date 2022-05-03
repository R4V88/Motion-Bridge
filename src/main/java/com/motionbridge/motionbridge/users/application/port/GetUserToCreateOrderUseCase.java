package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.users.entity.UserEntity;

import java.util.Optional;


public interface GetUserToCreateOrderUseCase {
    UserEntity getCurrentUserById(Long userId);
    Optional<UserEntity> getUserById(Long id);
}
