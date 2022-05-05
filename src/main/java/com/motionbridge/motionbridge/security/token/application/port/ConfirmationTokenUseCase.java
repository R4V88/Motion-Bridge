package com.motionbridge.motionbridge.security.token.application.port;

import com.motionbridge.motionbridge.security.token.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenUseCase {
    void saveConfirmationToken(ConfirmationToken token);

    Optional<ConfirmationToken> getToken(String token);

    Integer setConfirmedAt(String token);

    void deleteTokenByUserId(Long id);
}
