package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.users.db.ConfirmationTokenRepository;
import com.motionbridge.motionbridge.users.entity.ConfirmationToken;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConfirmationTokenService implements ConfirmationTokenUseCase {

    final ConfirmationTokenRepository confirmationTokenRepository;

    @Transactional
    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public Integer setConfirmedAt(String token) {
        return confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    @Transactional
    @Override
    public void deleteTokenByUserId(Long id) {
        confirmationTokenRepository.deleteByUserEntityId(id);
    }
}
