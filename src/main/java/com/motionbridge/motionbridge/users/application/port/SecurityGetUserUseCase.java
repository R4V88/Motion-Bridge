package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.users.entity.UserEntity;

import java.util.Optional;

public interface SecurityGetUserUseCase {

    Optional<UserEntity> findByUserEmailIgnoreCase(String email);
}
