package com.motionbridge.motionbridge.users.application.port;

import com.motionbridge.motionbridge.users.entity.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenUseCase {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> getToken(String token);

    Integer setConfirmedAt(String token);

    void deleteTokenByUserId(Long id);
}
