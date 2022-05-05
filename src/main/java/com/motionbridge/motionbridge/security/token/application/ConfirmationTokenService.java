package com.motionbridge.motionbridge.security.token.application;

import com.motionbridge.motionbridge.security.token.ConfirmationToken;
import com.motionbridge.motionbridge.security.token.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.security.token.db.ConfirmationTokenRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationTokenService implements ConfirmationTokenUseCase {

    final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public Integer setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    @Override
    public void deleteTokenByUserId(Long id) {
        confirmationTokenRepository.deleteByUserEntityId(id);
    }
}
