package com.motionbridge.motionbridge.users.application;

import com.motionbridge.motionbridge.users.application.port.UserDataManipulationUseCase;
import com.motionbridge.motionbridge.users.db.UserEntityRepository;
import com.motionbridge.motionbridge.users.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService implements UserDataManipulationUseCase {

    final UserEntityRepository repository;
    final PasswordEncoder encoder;

    @Transactional
    @Override
    public UpdatePasswordResponse updatePassword(UpdatePasswordCommand command) {
        return repository.findUserById(command.getId())
                .map(user -> {
                    updateActualPassword(command, user);
                    log.warn("Password has been changed");
                    return UpdatePasswordResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdatePasswordResponse(false, Collections.singletonList("New password is same as old")));
    }

    private void updateActualPassword(UpdatePasswordCommand command, UserEntity user) {
        if (!command.getPassword().equals(user.getPassword())) {
            user.setPassword(encoder.encode(command.getPassword()));
        }
    }

    @Transactional
    @Override
    public RegisterResponse register(String login, String username, String password, Boolean acceptedTerms, Boolean acceptedNewsletter) {
        if (repository.findByUsernameIgnoreCase(username).isPresent()) {
            return RegisterResponse.failure("Account already exists");
        }
        UserEntity entity = new UserEntity(login, username, encoder.encode(password), acceptedTerms, acceptedNewsletter);
        return RegisterResponse.success(repository.save(entity));
    }

    @Override
    public UserEntity retrieveOrderByUserId(Long id) {
        Optional<UserEntity> user = getUserById(id);
        UserEntity userEntity;
        if (user.isPresent()) {
            userEntity = user.get();
            return userEntity;
        } else {
            throw new NoSuchElementException("User with id: " + id + "does not exist");
        }
    }

    @Override
    public Optional<UserEntity> getUserById(Long id) {
        Optional<UserEntity> user = repository.findById(id);
        UserEntity userEntity;
        if (user.isPresent()) {
            userEntity = user.get();
            return Optional.of(userEntity);
        } else {
            throw new NoSuchElementException("User with id: " + id + "does not exist");
        }
    }

    @Override
    public UserEntity getCurrentUserById(Long userId) {
        Optional<UserEntity> userTemp = getUserById(userId);
        UserEntity userToGet;
        if (userTemp.isPresent()) {
            userToGet = userTemp.get();
        } else {
            userToGet = new UserEntity();
            log.info("User with id: " + userId + " not found");
        }
        return userToGet;
    }
}
