package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.security.PasswordEncoder;
import com.motionbridge.motionbridge.users.application.port.ConfirmationTokenUseCase;
import com.motionbridge.motionbridge.users.application.port.UserRegisterationUseCase;
import com.motionbridge.motionbridge.users.db.ConfirmationTokenRepository;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.ConfirmationToken;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConfirmationTokenServiceIT {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    ConfirmationTokenUseCase confirmationTokenUseCase;

    @Autowired
    UserRegisterationUseCase userRegisterationUseCase;

    @Autowired
    UserEntityRepository userEntityRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void tokenShouldBeSaved() {
        //GIVEN
        String token = "tokenTestowy";
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;
        final UserEntity insertedUser = userEntityRepository.save(new UserEntity(login, email, passwordEncoder.bCryptPasswordEncoder().encode(password), acceptTerms, acceptNewsLetter));
        final Long registeredUserId = insertedUser.getId();
        final Optional<UserEntity> user = userEntityRepository.findById(registeredUserId);
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user.get());

        //WHEN
        confirmationTokenUseCase.saveConfirmationToken(confirmationToken);

        //THEN
        final ConfirmationToken tokenFromDataBase = confirmationTokenRepository.findConfirmationTokenByUserEntity_Id(registeredUserId);
        assertNotNull(tokenFromDataBase.getToken());
    }

    @Test
    void tokenShouldBeRetrieved() {
        //GIVEN
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;
        final UserEntity insertedUser = userEntityRepository.save(new UserEntity(login, email, passwordEncoder.bCryptPasswordEncoder().encode(password), acceptTerms, acceptNewsLetter));
        String token = "tokenTestowy";
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusMinutes(15);
        ConfirmationToken confirmationToken = new ConfirmationToken(token, currentTime, endTime, insertedUser);
        confirmationTokenRepository.save(confirmationToken);

        //WHEN
        final Optional<ConfirmationToken> tokenFromDatabase = confirmationTokenUseCase.getToken(token);

        //THEN
        assertEquals(token, tokenFromDatabase.get().getToken());
        assertEquals(insertedUser.getId(), tokenFromDatabase.get().getUserEntity().getId());
    }

    @Test
    void tokenShouldBeConfirmedSuccessfully() {
        //GIVEN
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;
        final UserEntity insertedUser = userEntityRepository.save(new UserEntity(login, email, passwordEncoder.bCryptPasswordEncoder().encode(password), acceptTerms, acceptNewsLetter));
        String token = "tokenTestowy";
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusMinutes(15);
        ConfirmationToken confirmationToken = new ConfirmationToken(token, currentTime, endTime, insertedUser);
        confirmationTokenRepository.save(confirmationToken);

        //WHEN
        confirmationTokenUseCase.setConfirmedAt(token);

        //THEN
        final Optional<ConfirmationToken> tokenFromDatabase = confirmationTokenRepository.findByToken(token);

        assertEquals(token, tokenFromDatabase.get().getToken());
        assertNotNull(tokenFromDatabase.get().getConfirmedAt());
    }

    @Test
    void tokenShouldBeDeletedSuccessfully() {
        //GIVEN
        String login = "test";
        String email = "test@test.com";
        String password = "testowe1244!";
        Boolean acceptTerms = true;
        Boolean acceptNewsLetter = true;
        final UserEntity insertedUser = userEntityRepository.save(new UserEntity(login, email, passwordEncoder.bCryptPasswordEncoder().encode(password), acceptTerms, acceptNewsLetter));
        String token = "tokenTestowy";
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime endTime = currentTime.plusMinutes(15);
        ConfirmationToken confirmationToken = new ConfirmationToken(token, currentTime, endTime, insertedUser);
        confirmationTokenRepository.save(confirmationToken);

        confirmationTokenRepository.findByToken(token);

        //WHEN
        confirmationTokenUseCase.deleteTokenByUserId(insertedUser.getId());

        //THEN
        final Optional<ConfirmationToken> tokenFromDatabase = confirmationTokenRepository.findByToken(token);
        boolean isPresent = tokenFromDatabase.isPresent();
        assertFalse(isPresent);

    }
}