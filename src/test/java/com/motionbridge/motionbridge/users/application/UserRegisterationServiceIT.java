package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import com.motionbridge.motionbridge.users.db.ConfirmationTokenRepository;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.ConfirmationToken;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserRegisterationServiceIT {

    @Autowired
    UserRegisterationUseCase userRegisterationUseCase;

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    ConfirmationTokenUseCase confirmationTokenUseCase;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Test
    void shouldSuccesfullyCreateNewUser() {
        //GIVEN
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;

        //WHEN
        final UserRegisterationUseCase.RegisterResponse registerResponse =
                userRegisterationUseCase.register(login, email, password, acceptTerms, acceptNewsLetter);
        final Long registeredUserId = registerResponse.getRight().getId();

        //THEN
        final Optional<UserEntity> userFromDatabase = userEntityRepository.findById(registeredUserId);
        assertEquals(login, userFromDatabase.get().getLogin());
        assertEquals(email, userFromDatabase.get().getEmail());
        Assertions.assertNotNull(userFromDatabase.get().getPassword());
        assertEquals(acceptTerms, userFromDatabase.get().getAcceptedTerms());
        assertEquals(acceptNewsLetter, userFromDatabase.get().getAcceptedNewsletter());
        Assertions.assertFalse(userFromDatabase.get().getIsBlocked());
        Assertions.assertFalse(userFromDatabase.get().getIsVerified());
        assertEquals("ROLE_USER", userFromDatabase.get().getRoles().stream().toList().get(0).toString());
    }

    @Test
    void shouldSuccesfullyConfirmToken() {
        //GIVEN
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;
        final UserRegisterationUseCase.RegisterResponse registerResponse = userRegisterationUseCase.register(login, email, password, acceptTerms, acceptNewsLetter);
        final Long registeredUserId = registerResponse.getRight().getId();
        final Optional<UserEntity> userFromDatabase = userEntityRepository.findById(registeredUserId);
        final ConfirmationToken confirmationToken = confirmationTokenRepository.findConfirmationTokenByUserEntity_Id(registeredUserId);
        final String token = confirmationToken.getToken();

        //WHEN
        userRegisterationUseCase.confirmToken(token);
        final Optional<UserEntity> userAfterTokenConfirmation = userEntityRepository.findById(registeredUserId);
        final ConfirmationToken confirmationTokenAfeterTokenConfirmation = confirmationTokenRepository.findConfirmationTokenByUserEntity_Id(registeredUserId);

        //THEN
        Assertions.assertNotEquals(userFromDatabase.get().getIsVerified(), userAfterTokenConfirmation.get().getIsVerified());
        assertTrue(userAfterTokenConfirmation.get().getIsVerified());
        Assertions.assertNotNull(confirmationTokenAfeterTokenConfirmation.getConfirmedAt());
    }
}