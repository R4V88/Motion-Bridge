package com.motionbridge.motionbridge.security.token.application.port;

import com.motionbridge.motionbridge.security.token.ConfirmationToken;

public interface ConfirmationTokenUseCase {
    void saveConfirmationToken(ConfirmationToken token);
}
