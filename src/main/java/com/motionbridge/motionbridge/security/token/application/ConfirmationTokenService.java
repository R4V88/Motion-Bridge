package com.motionbridge.motionbridge.security.token.application;

import com.motionbridge.motionbridge.security.token.ConfirmationToken;
import com.motionbridge.motionbridge.security.token.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.security.token.db.ConfirmationTokenRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationTokenService implements ConfirmationTokenUseCase {

    final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }
}
