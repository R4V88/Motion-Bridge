package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.commons.Either;
import com.motionbridge.motionbridge.security.token.ConfirmationToken;
import com.motionbridge.motionbridge.security.token.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import com.motionbridge.motionbridge.users.application.validators.EmailValidator;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRegisterationService implements UserRegisterationUseCase {

    final EmailValidator emailValidator;
    final ConfirmationTokenUseCase confirmationTokenUseCase;
    final UserEntityRepository repository;
    final PasswordEncoder encoder;

    @Transactional
    @Override
    public RegisterResponse register(String login, String email, String password, Boolean acceptedTerms, Boolean acceptedNewsletter) {
        if (repository.findByEmailIgnoreCase(email).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        if (!emailValidator.test(email)) {
            return RegisterResponse.failure("Email is not valid");
        }
        UserEntity entity = new UserEntity(login, email, encoder.encode(password), acceptedTerms, acceptedNewsletter);
        UserEntity saveUser = repository.save(entity);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                saveUser
        );
        confirmationTokenUseCase.saveConfirmationToken(confirmationToken);

        //TODO: SEND EMAIL
        //return token
        return RegisterResponse.success(saveUser);
    }

    @Override
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenUseCase
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenUseCase.setConfirmedAt(token);
        enableUser(
                confirmationToken.getUserEntity().getEmail());
        return "confirmed";
    }

    public int enableUser(String email) {
        return repository.enableAppUser(email);
    }

}
